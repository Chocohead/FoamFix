package pl.asie.foamfix.mixin.thready;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;

import org.objectweb.asm.Opcodes;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.datafixers.util.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemModelGenerator;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelBakery.BlockStateDefinitionException;
import net.minecraft.client.renderer.model.ModelBakery.ModelListWrapper;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.AtlasTexture.SheetData;
import net.minecraft.client.renderer.texture.SpriteMap;
import net.minecraft.profiler.EmptyProfiler;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.TransformationMatrix;

import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.registries.ForgeRegistries;

import pl.asie.foamfix.blob.CacheController;
import pl.asie.foamfix.thready.ModelKey;
import pl.asie.foamfix.thready.ParallelBaker;

@Mixin(ModelBakery.class)
abstract class ModelBakeryMixin {
	@Shadow
	protected static @Final Set<RenderMaterial> LOCATIONS_BUILTIN_TEXTURES;
	@Shadow
	private static @Final Logger LOGGER;
	@Shadow
	private static @Final String MODEL_MISSING_STRING;
	@Shadow
	private static @Final ItemModelGenerator ITEM_MODEL_GENERATOR;
	@Shadow
	private static @Final Map<ResourceLocation, StateContainer<Block, BlockState>> STATE_CONTAINER_OVERRIDES;
	@Shadow
	protected @Final IResourceManager resourceManager;
	@Shadow
	private SpriteMap spriteMap;
	@Shadow
	private @Final Set<ResourceLocation> unbakedModelLoadingQueue;
	@Shadow
	private @Final Map<ResourceLocation, IUnbakedModel> unbakedModels;
	@Shadow
	private @Final Map<ModelKey, IBakedModel> bakedModels;
	@Shadow
	@Mutable
	private @Final Map<ResourceLocation, IUnbakedModel> topUnbakedModels;
	@Shadow
	private @Final Map<ResourceLocation, IBakedModel> topBakedModels;
	@Shadow
	private Map<ResourceLocation, Pair<AtlasTexture, SheetData>> sheetData;
	@Unique
	private final AtomicInteger modelCounter = new AtomicInteger(1);
	@Shadow
	private @Final Object2IntMap<BlockState> stateModelIds;
	@Unique
	private boolean isLoadingModels;
	@Unique
	private final ThreadLocal<Map<ResourceLocation, IUnbakedModel>> loadedModels = ThreadLocal.withInitial(Object2ObjectOpenHashMap::new);
	@Unique
	private final ReentrantLock lock = new ReentrantLock();

	@Overwrite(remap = false)
	protected void processLoading(IProfiler profiler, int maxMipmapLevel) {
		//profiler = new Profiler(Util.nanoTimeSupplier, () -> 0, false);
		ModelLoaderRegistry.onModelLoadingStart();

		profiler.startSection("missing_model");
		IUnbakedModel missingModel;
		try {
			unbakedModels.put(ModelBakery.MODEL_MISSING, missingModel = loadModel(ModelBakery.MODEL_MISSING));
		} catch (IOException e) {
			LOGGER.error("Error loading missing model, should never happen :(", e);
			throw new RuntimeException(e);
		}

		profiler.endStartSection("static_definitions");
		CompletableFuture<Set<? extends ResourceLocation>> overridenBlocks = CompletableFuture.supplyAsync(() -> {
			Set<ModelResourceLocation> models = new ObjectOpenHashSet<>();

			STATE_CONTAINER_OVERRIDES.forEach((location, container) -> {
				/*for (BlockState state : container.getValidStates()) {
					models.add(BlockModelShapes.getModelLocation(location, state));
				}*/
				models.add(BlockModelShapes.getModelLocation(container.getBaseState()));
			});

			return models;
		}, Util.getServerExecutor());

		profiler.endStartSection("blocks");
		CompletableFuture<Set<? extends ResourceLocation>> blockModels = CompletableFuture.supplyAsync(() -> {
			Set<ModelResourceLocation> models = new ObjectOpenHashSet<>();

			for (Block block : ForgeRegistries.BLOCKS) {
				/*for (BlockState state : block.getStateContainer().getValidStates()) {
					models.add(BlockModelShapes.getModelLocation(state));
				}*/
				models.add(BlockModelShapes.getModelLocation(block.getStateContainer().getBaseState()));
			}

			return models;
		}, Util.getServerExecutor());

		profiler.endStartSection("items");
		CompletableFuture<Set<? extends ResourceLocation>> itemModels = CompletableFuture.supplyAsync(() -> {
			Set<ModelResourceLocation> models = new ObjectOpenHashSet<>();

			for (ResourceLocation location : ForgeRegistries.ITEMS.getKeys()) {
				models.add(new ModelResourceLocation(location, "inventory"));
			}

			return models;
		}, Util.getServerExecutor());

		profiler.endStartSection("special");
		CompletableFuture<Set<? extends ResourceLocation>> specialModels = CompletableFuture.supplyAsync(() -> {
			Set<ResourceLocation> models = new ObjectOpenHashSet<>();

			models.add(new ModelResourceLocation("minecraft:trident_in_hand#inventory"));
			models.addAll(getSpecialModels());

			return models;
		}, Util.getServerExecutor());

		//Split out the actual work so it's measurable too
		profiler.endStartSection("FOAMFIX");

		profiler.startSection("model_merging");
		SortedSet<ResourceLocation> modelQueue = new ObjectLinkedOpenHashSet<>();
		for (CompletableFuture<Set<? extends ResourceLocation>> task : Arrays.asList(overridenBlocks, blockModels, itemModels, specialModels)) {
			modelQueue.addAll(task.join());
		}
		modelQueue.remove(ModelBakery.MODEL_MISSING); //This is already done
		profiler.endSection();

		isLoadingModels = true;
		topUnbakedModels = ParallelBaker.bake(profiler, modelQueue, unbakedModels, location -> {
			Map<ResourceLocation, IUnbakedModel> localModels = loadedModels.get();
			localModels.clear();

			try {
				loadBlockstate(location);
			} catch (BlockStateDefinitionException e) {
				LOGGER.warn(e.getMessage());
				localModels.put(location, missingModel);
			} catch (FileNotFoundException e) {
				LOGGER.warn("Unable to find model: '{}' at {}", location, e.getMessage());
				localModels.put(location, missingModel);
			} catch (Exception e) {
				LOGGER.warn("Unable to load model: '{}':", location, e);
				localModels.put(location, missingModel);
			}

			return localModels;
		});
		isLoadingModels = false;

		//All the models have been created now, back to what vanilla did
		profiler.endSection();

		profiler.endStartSection("textures");
		Set<Pair<String, String>> missingTextures = ConcurrentHashMap.newKeySet();
		List<CompletableFuture<Collection<RenderMaterial>>> textureTasks = new ArrayList<>(topUnbakedModels.size());

		for (IUnbakedModel model : topUnbakedModels.values()) {
			textureTasks.add(CompletableFuture.supplyAsync(() -> {
				return model.getTextures(this::getUnbakedModel, missingTextures);
			}, Util.getServerExecutor()));
		}

		//Reverse the order of addition because HashSets replace with a duplicate add
		Set<RenderMaterial> usedTextures = new ObjectOpenHashSet<>();
		ForgeHooksClient.gatherFluidTextures(usedTextures);
		usedTextures.addAll(LOCATIONS_BUILTIN_TEXTURES);
		for (CompletableFuture<Collection<RenderMaterial>> task : textureTasks) {
			usedTextures.addAll(task.join());
		}
		Map<ResourceLocation, List<RenderMaterial>> atlasMap = usedTextures.stream().collect(Collectors.groupingBy(RenderMaterial::getAtlasLocation));

		profiler.endStartSection("stitching");
		List<CompletableFuture<Pair<AtlasTexture, SheetData>>> stitchTasks = new ArrayList<>(atlasMap.size());

		for (Entry<ResourceLocation, List<RenderMaterial>> entry : atlasMap.entrySet()) {
			AtlasTexture atlas = new AtlasTexture(entry.getKey());
			List<RenderMaterial> sprites = entry.getValue();

			stitchTasks.add(CompletableFuture.supplyAsync(() -> {
				return Pair.of(atlas, atlas.stitch(resourceManager, sprites.stream().map(RenderMaterial::getTextureLocation), EmptyProfiler.INSTANCE, maxMipmapLevel));
			}, Util.getServerExecutor()));
		}

		for (Entry<String, Set<String>> entry : missingTextures.stream().collect(Collectors.groupingBy(Pair::getSecond, Collectors.mapping(Pair::getFirst, Collectors.toSet()))).entrySet()) {
			String model = entry.getKey();
			if (MODEL_MISSING_STRING.equals(model)) continue;

			LOGGER.warn("Unable to resolve texture references in {}:{}\t{}", model, System.lineSeparator(), String.join(System.lineSeparator().concat("\t"), entry.getValue()));
		}

		sheetData = new Object2ObjectArrayMap<>(stitchTasks.size());
		for (CompletableFuture<Pair<AtlasTexture, SheetData>> task : stitchTasks) {
			Pair<AtlasTexture, SheetData> result = task.join();
			sheetData.put(result.getFirst().getTextureLocation(), result);
		}

		profiler.endSection();
	}

	@Redirect(method = "uploadTextures", at = @At(value = "INVOKE", target = "Ljava/util/Map;keySet()Ljava/util/Set;", remap = false))
	private Set<ResourceLocation> doBaking(Map<ResourceLocation, IUnbakedModel> topUnbakedModels) {
		CacheController.loadCache(topUnbakedModels, bakedModels, topBakedModels);
		TransformationMatrix rotation = ModelRotation.X0_Y0.getRotation();
		boolean uvLock = ModelRotation.X0_Y0.isUvLock();
		long start = System.nanoTime();

		int cheapHit = 0;
		for (Entry<ResourceLocation, IUnbakedModel> entry : topUnbakedModels.entrySet()) {
			IUnbakedModel unbakedModel = entry.getValue();

			IBakedModel bakedModel;
			try {
				BlockModel blockModel;
				if (unbakedModel instanceof BlockModel && ((blockModel = (BlockModel) unbakedModel).getRootModel() == ModelBakery.MODEL_GENERATED)) {
					cheapHit++;
					bakedModel = ITEM_MODEL_GENERATOR.makeItemModel(spriteMap::getSprite, blockModel).bakeModel((ModelBakery) (Object) this, blockModel, spriteMap::getSprite, ModelRotation.X0_Y0, entry.getKey(), false);
				} else {
					ModelKey key = new ModelKey(entry.getKey(), rotation, uvLock);

					if (bakedModels.containsKey(key)) {
						bakedModel = bakedModels.get(key);
					} else {
						bakedModel = unbakedModel.bakeModel((ModelBakery) (Object) this, spriteMap::getSprite, ModelRotation.X0_Y0, key.location);
						bakedModels.put(key, bakedModel);
					}
				}
			} catch (Exception e) {
				LOGGER.warn("Unable to bake model: '{}':", entry.getKey(), e);
				bakedModel = null;
			}

			if (bakedModel != null) topBakedModels.put(entry.getKey(), bakedModel);
		}

		long end = System.nanoTime();
		LOGGER.error("Finished baking {} models ({} cheap) taking {} seconds, had {} cache hits and {} cache misses ({} total)",
						topUnbakedModels.size(), cheapHit, (end - start) / 1_000_000_000, cacheHit, cacheMiss, cacheHit + cacheMiss);
		return Collections.emptySet();
	}

	@Shadow
	public abstract IUnbakedModel getUnbakedModel(ResourceLocation modelLocation);

	@Inject(method = "getUnbakedModel", at = @At(value = "FIELD", ordinal = 0, opcode = Opcodes.GETFIELD,
													target = "Lnet/minecraft/client/renderer/model/ModelBakery;unbakedModelLoadingQueue:Ljava/util/Set;"))
	private void beCareful(CallbackInfoReturnable<IUnbakedModel> call) {
		lock.lock();
	}

	@Inject(method = "getUnbakedModel", at = {@At(value = "RETURN", ordinal = 1),
												@At(value = "INVOKE", target = "Ljava/lang/IllegalStateException;<init>(Ljava/lang/String;)V", shift = Shift.AFTER, remap = false)})
	private void carefulOver(CallbackInfoReturnable<IUnbakedModel> call) {
		assert lock.isHeldByCurrentThread();
		unbakedModelLoadingQueue.clear(); //Don't leave anything behind for the next use
		lock.unlock();
	}

	@Shadow
	private void loadBlockstate(ResourceLocation location) throws Exception {
		throw new AssertionError("Shadow didn't apply");
	}

	@Redirect(method = "loadBlockstate", at = @At(value = "FIELD", ordinal = 0, opcode = Opcodes.GETFIELD,
													target = "Lnet/minecraft/client/renderer/model/ModelBakery;unbakedModels:Ljava/util/Map;"))
	private Map<ResourceLocation, IUnbakedModel> grabModel(ModelBakery self) {
		return isLoadingModels ? loadedModels.get() : unbakedModels;
	}

	@Redirect(method = "loadBlockstate", at = @At(value = "INVOKE", target = "Ljava/util/HashMap;forEach(Ljava/util/function/BiConsumer;)V", remap = false))
	private void registerModelIDs(HashMap<ModelListWrapper, Set<BlockState>> models, BiConsumer<ModelListWrapper, Set<BlockState>> lambda) {
		Reference2IntMap<BlockState> stateToID = new Reference2IntArrayMap<>();

		for (Set<BlockState> states : models.values()) {
			for (Iterator<BlockState> it = states.iterator(); it.hasNext();) {
				BlockState state = it.next();

				if (state.getRenderType() != BlockRenderType.MODEL) {
					it.remove();
					stateToID.put(state, 0);
				}
			}

            if (states.size() > 1) {
            	int ID = modelCounter.getAndIncrement();

            	for (BlockState state : states) {
            		stateToID.put(state, ID);
            	}
            }
		}

		if (!stateToID.isEmpty()) {
			Util.getServerExecutor().execute(() -> {
				synchronized (stateModelIds) {
					stateModelIds.putAll(stateToID);
				}
			});
		}
	}

	@Inject(method = "putModel", at = @At("HEAD"), cancellable = true)
	private void grabModel(ResourceLocation location, IUnbakedModel model, CallbackInfo call) {
		if (isLoadingModels) {
			loadedModels.get().put(location, model);
			call.cancel();
		}
	}

	@Overwrite(remap = false)
	private void addModelToCache(ResourceLocation location) {
		throw new UnsupportedOperationException("Unexpectedly loaded model for " + location);
	}

	@Overwrite
	private void loadTopModel(ModelResourceLocation location) {
		throw new UnsupportedOperationException("Unexpectedly loaded model for " + location);
	}

	@Overwrite
	private void registerModelIds(Iterable<BlockState> states) {
		throw new UnsupportedOperationException("Unexpectedly registered model IDs for " + states);
	}

	@Shadow
	protected abstract BlockModel loadModel(ResourceLocation location) throws IOException;

	@Shadow(remap = false)
	public abstract Set<ResourceLocation> getSpecialModels();

	@Unique
	private int cacheHit, cacheMiss;

	@Inject(method = "getBakedModel", remap = false,
			at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;", remap = false))
	private void cacheHit(CallbackInfoReturnable<IBakedModel> call) {
		cacheHit++;
	}

	@Inject(method = "getBakedModel", remap = false,
			at = @At(value = "INVOKE", remap = true,
					target = "Lnet/minecraft/client/renderer/model/ModelBakery;getUnbakedModel(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/client/renderer/model/IUnbakedModel;"))
	private void cacheMiss(CallbackInfoReturnable<IBakedModel> call) {
		cacheMiss++;
	}
}