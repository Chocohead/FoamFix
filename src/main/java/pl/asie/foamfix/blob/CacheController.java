package pl.asie.foamfix.blob;

import java.nio.file.Files;
import java.nio.file.Path;

import net.minecraftforge.fml.loading.FMLPaths;

import pl.asie.foamfix.FoamyConfig;

public class CacheController {
	public static final Path MODEL_CACHE = FMLPaths.GAMEDIR.get().resolve("serialised_baked_models.txt");
	public static final Path MODEL_BROKEN_CACHE = MODEL_CACHE.resolveSibling("broken_serialised_baked_models.txt");
	public static final Path MODEL_LOGS = FMLPaths.GAMEDIR.get();

	public static boolean logModelSurface() {
		return FoamyConfig.LOG_SURFACE_MODELS.asBoolean();
	}

	public static boolean logFullModels() {
		return FoamyConfig.LOG_ALL_MODELS.asBoolean();
	}

	public static boolean hasCache() {
		return Files.exists(MODEL_CACHE); //TODO: Compare loaded unbaked models, resource packs?
	}

	public static boolean doCompare() {
		return FoamyConfig.COMPARE_CACHED_MODELS.asBoolean();
	}
}