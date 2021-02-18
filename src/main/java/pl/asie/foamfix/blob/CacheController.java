package pl.asie.foamfix.blob;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.TransformationMatrix;

import net.minecraftforge.fml.loading.FMLPaths;

import pl.asie.foamfix.FoamyConfig;
import pl.asie.foamfix.thready.ModelKey;

public class CacheController {
	public static final Path MODEL_CACHE = FMLPaths.GAMEDIR.get().resolve("serialised_baked_models.txt");
	public static final Path MODEL_BROKEN_CACHE = MODEL_CACHE.resolveSibling("broken_serialised_baked_models.txt");
	public static final Path REJECTED_MODEL_CACHE = MODEL_CACHE.resolveSibling("rejected_baked_models.txt");
	public static final Path MODEL_LOGS = FMLPaths.GAMEDIR.get();

	public static boolean logModelSurface() {
		return FoamyConfig.LOG_SURFACE_MODELS.asBoolean();
	}

	public static boolean logFullModels() {
		return FoamyConfig.LOG_ALL_MODELS.asBoolean();
	}

	public static boolean logSerializableModels() {
		return true;
	}

	public static boolean hasCache() {
		return FoamyConfig.CACHE_MODELS.asBoolean() && Files.exists(MODEL_CACHE) && Files.exists(REJECTED_MODEL_CACHE); //TODO: Compare loaded unbaked models, resource packs?
	}

	public static void loadCache(Map<ResourceLocation, IUnbakedModel> topUnbakedModels, Map<ModelKey, IBakedModel> bakedModels, Map<ResourceLocation, IBakedModel> topBakedModels) {
		if (!hasCache()) return;

		Stopwatch timer = Stopwatch.createStarted();
		Map<?, IBakedModel> remade = ModelSerialiser.deserialise(CacheController.MODEL_CACHE);
		Set<ResourceLocation> rejects = ModelSerialiser.deserialiseRejects(CacheController.REJECTED_MODEL_CACHE);
		timer.stop();

		ModelSerialiser.LOGGER.info("Read in {} models and {} skips, taking {}ms", remade.size(), rejects.size(), timer.elapsed(TimeUnit.MILLISECONDS));
		TransformationMatrix rotation = ModelRotation.X0_Y0.getRotation();
		boolean uvLock = ModelRotation.X0_Y0.isUvLock();

		timer.start();
		for (Iterator<ResourceLocation> it = topUnbakedModels.keySet().iterator(); it.hasNext();) {
			ResourceLocation location = it.next();

			if (!rejects.contains(location)) {
				it.remove(); //Won't need you anymore

				ModelKey key = new ModelKey(location, rotation, uvLock);
				IBakedModel model = remade.get(key);

				bakedModels.put(key, model);
				if (model != null) topBakedModels.put(location, model);
			}
		}
		timer.stop();

		ModelSerialiser.LOGGER.info("Loaded {} models ({} top), leaving {}, taking {}ms",
										bakedModels.size(), topBakedModels.size(), topUnbakedModels.size(), timer.elapsed(TimeUnit.MILLISECONDS));
	}

	public static boolean doCompare() {
		return FoamyConfig.COMPARE_CACHED_MODELS.asBoolean();
	}
}