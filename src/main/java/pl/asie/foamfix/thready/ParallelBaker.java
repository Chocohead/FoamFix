package pl.asie.foamfix.thready;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

public class ParallelBaker {
	private static class InitialQueue extends ArrayDeque<ResourceLocation> implements BlockingQueue<ResourceLocation> {
		private static final long serialVersionUID = 8985374193017312047L;

		@Override
		public void put(ResourceLocation model) throws InterruptedException {
			add(model); //Presumed unbounded queue
		}

		@Override
		public boolean offer(ResourceLocation model, long timeout, TimeUnit unit) throws InterruptedException {
			return offer(model);
		}

		@Override
		public ResourceLocation take() throws InterruptedException {
			if (!isEmpty()) return remove();
			throw new UnsupportedOperationException();
		}

		@Override
		public ResourceLocation poll(long timeout, TimeUnit unit) throws InterruptedException {
			if (!isEmpty()) return poll();
			throw new UnsupportedOperationException();
		}

		@Override
		public int remainingCapacity() {
			return Integer.MAX_VALUE; //Presumed unbounded queue
		}

		@Override
		public int drainTo(Collection<? super ResourceLocation> pool) {
			pool.addAll(this);
			int moved = size();
			clear();
			return moved;
		}

		@Override
		public int drainTo(Collection<? super ResourceLocation> pool, int maxElements) {
			int moved = 0;

			while (moved < maxElements && !isEmpty()) {
				pool.add(remove());
			}

			return moved;
		}
	}

	public static Map<ResourceLocation, IUnbakedModel> bake(IProfiler profiler, Set<ResourceLocation> knownModels, Map<ResourceLocation, IUnbakedModel> allModels,
															Function<ResourceLocation, Map<ResourceLocation, IUnbakedModel>> baker) {
		profiler.startSection("namespace_allocation");
		Map<String, BlockingQueue<ResourceLocation>> namespacedModels = new Object2ObjectOpenHashMap<>();
		for (ResourceLocation model : knownModels) {
			namespacedModels.computeIfAbsent(model.getNamespace(), k -> new InitialQueue()).add(model);
		}

		profiler.endStartSection("creation");

		assert namespacedModels.values().stream().mapToInt(Queue::size).sum() == knownModels.size();
		Map<ResourceLocation, IUnbakedModel> topLevelModels = new Object2ObjectOpenHashMap<>(knownModels.size() + 1);
		topLevelModels.putAll(allModels); //Only the missing model

		//A more parallel access friendly queue for model dependencies to pour into
		Map<String, BlockingQueue<ResourceLocation>> parentModels = Minecraft.getInstance().getResourceManager().getResourceNamespaces().stream()
																								.collect(Collectors.toMap(Function.identity(), k -> new LinkedBlockingQueue<>()));
		Set<ResourceLocation> allParents = ConcurrentHashMap.newKeySet(knownModels.size());
		allParents.addAll(knownModels); //Clone the model map to be more thread safe

		runStep(allParents, topLevelModels, namespacedModels, parentModels, baker);
		allModels.putAll(topLevelModels);

		boolean didWork;
		do {
			didWork = runStep(allParents, allModels, parentModels, parentModels, baker);
		} while (didWork);

		return topLevelModels;
	}

	private static boolean runStep(Set<ResourceLocation> knownModels, Map<ResourceLocation, IUnbakedModel> done, Map<String, BlockingQueue<ResourceLocation>> namespacedModels,
									Map<String, ? extends Queue<ResourceLocation>> parents, Function<ResourceLocation, Map<ResourceLocation, IUnbakedModel>> baker) {
		List<CompletableFuture<Map<ResourceLocation, IUnbakedModel>>> tasks = new ArrayList<>(namespacedModels.size());

		for (Entry<String, BlockingQueue<ResourceLocation>> entry : namespacedModels.entrySet()) {
			if (entry.getValue().isEmpty()) continue; //Avoid making a task to produce an empty map

			List<ResourceLocation> models = new ArrayList<>();
			entry.getValue().drainTo(models);
			System.out.println("Drained " + models.size());

			tasks.add(CompletableFuture.supplyAsync(() -> {
				Map<ResourceLocation, IUnbakedModel> out = new Object2ObjectOpenHashMap<>(models.size());

				for (ResourceLocation model : models) {
					if (out.containsKey(model)) {
						System.err.println("Found duplicate for " + model);
						continue;
					}
					//System.out.println("Baked " + model);

					Map<ResourceLocation, IUnbakedModel> madeModels = baker.apply(model);
					out.putAll(madeModels);

					for (IUnbakedModel madeModel : madeModels.values()) {
						for (ResourceLocation dependency : madeModel.getDependencies()) {
							if (knownModels.add(dependency)) {
								Queue<ResourceLocation> parent = parents.get(dependency.getNamespace());
								if (parent == null) {
									System.err.println("Dependent model at " + dependency + " from unknown namespace: " + dependency.getNamespace());
									continue;
								}
								parent.add(dependency);
							}
						}
					}
				}

				return out;
			}, Util.getServerExecutor()));
		}

		for (CompletableFuture<Map<ResourceLocation, IUnbakedModel>> task : tasks) {
			done.putAll(task.join());
		}

		return !tasks.isEmpty();
	}
}