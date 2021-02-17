package pl.asie.foamfix.mixin.blob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;

import pl.asie.foamfix.blob.CacheController;
import pl.asie.foamfix.blob.ModelClasses;
import pl.asie.foamfix.blob.ModelSerialiser;

@Mixin(ModelManager.class)
abstract class ModelManagerMixin {
	@Inject(method = "apply",
			at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/ForgeHooksClient;onModelBake(Lnet/minecraft/client/renderer/model/ModelManager;Ljava/util/Map;Lnet/minecraftforge/client/model/ModelLoader;)V", remap = false))
	private void beforeEvent(ModelBakery bakery, IResourceManager resourceManager, IProfiler profiler, CallbackInfo callback) {
		profiler.endStartSection("model_pre-stats");

		if (CacheController.logFullModels()) {
			profiler.startSection("unbaked");
			ModelClasses.note(CacheController.MODEL_LOGS.resolve("unbaked_premodels.txt"), ((ModelBakeryAccess) bakery).getUnbakedModels().values());
			profiler.endStartSection("baked");
			ModelClasses.note(CacheController.MODEL_LOGS.resolve("baked_premodels.txt"), ((ModelBakeryAccess) bakery).getBakedModels().values());
			profiler.endSection();
		}

		profiler.endStartSection("model_bake_event");
	}

	@Inject(method = "apply", 
			at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/IProfiler;endStartSection(Ljava/lang/String;)V", args = "ldc=cache"))
	private void onceDone(ModelBakery bakery, IResourceManager resourceManager, IProfiler profiler, CallbackInfo callback) {
		boolean logAllModels = CacheController.logFullModels();
		boolean logModelSurface = CacheController.logModelSurface();
		if (logModelSurface || logAllModels) {
			profiler.endStartSection("model_stats");

			if (logAllModels) {
				profiler.startSection("unbaked");
				ModelClasses.note(CacheController.MODEL_LOGS.resolve("unbaked_models.txt"), ((ModelBakeryAccess) bakery).getUnbakedModels().values()/*, true*/);
				profiler.endStartSection("baked");
				ModelClasses.note(CacheController.MODEL_LOGS.resolve("baked_models.txt"), ((ModelBakeryAccess) bakery).getBakedModels().values(), true);
				profiler.endSection();
			}

			if (logModelSurface) {
				profiler.startSection("surface_baked");
				Set<IBakedModel> modelsToWrite = new ReferenceOpenHashSet<>(bakery.getTopBakedModels().values());
				modelsToWrite.addAll(((ModelBakeryAccess) bakery).getBakedModels().values());
				ModelClasses.noteMain(CacheController.MODEL_LOGS.resolve("baked_top_models.txt"), modelsToWrite);
				profiler.endSection();
			}
		}

		profiler.endStartSection("model_serialisation");
		if (!CacheController.hasCache()) {
			profiler.startSection("serialise");
			try {
				ModelSerialiser.serialise(((ModelBakeryAccess) bakery).getBakedModels(), bakery.getTopBakedModels(), CacheController.MODEL_CACHE);
				profiler.endStartSection("optimise");
				ModelSerialiser.optimise(CacheController.MODEL_CACHE);
			} catch (Throwable t) {
				try {
					if (Files.exists(CacheController.MODEL_CACHE)) {//If it goes wrong move any remnants out of the way
						Files.move(CacheController.MODEL_CACHE, CacheController.MODEL_BROKEN_CACHE, StandardCopyOption.REPLACE_EXISTING);
					}
				} catch (IOException e) {
					t.addSuppressed(e);
				}
				throw t;
			} finally {
				profiler.endSection(); //Keep the profile state stack intact
			}

			if (CacheController.doCompare()) {
				profiler.startSection("deserialise");
				Map<?, IBakedModel> remade = ModelSerialiser.deserialise(CacheController.MODEL_CACHE);
				profiler.endStartSection("compare");
				ModelSerialiser.compare(remade);
				profiler.endSection();
			}
		}
	}
}