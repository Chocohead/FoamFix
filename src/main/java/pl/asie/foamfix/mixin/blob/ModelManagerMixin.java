package pl.asie.foamfix.mixin.blob;

import java.nio.file.Path;
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

import net.minecraftforge.fml.loading.FMLPaths;

import pl.asie.foamfix.blob.ModelClasses;
import pl.asie.foamfix.blob.ModelSerialiser;

@Mixin(ModelManager.class)
abstract class ModelManagerMixin {
	@Inject(method = "apply",
			at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/ForgeHooksClient;onModelBake(Lnet/minecraft/client/renderer/model/ModelManager;Ljava/util/Map;Lnet/minecraftforge/client/model/ModelLoader;)V", remap = false))
	private void beforeEvent(ModelBakery bakery, IResourceManager resourceManager, IProfiler profiler, CallbackInfo callback) {
		profiler.endStartSection("model_pre-stats");

		profiler.startSection("unbaked");
		//ModelClasses.note(FMLPaths.GAMEDIR.get().resolve("unbaked_premodels.txt"), ((ModelBakeryAccess) bakery).getUnbakedModels().values());
		profiler.endStartSection("baked");
		//ModelClasses.note(FMLPaths.GAMEDIR.get().resolve("baked_premodels.txt"), ((ModelBakeryAccess) bakery).getBakedModels().values());
		profiler.endSection();

		profiler.endStartSection("model_bake_event");
	}

	@Inject(method = "apply", 
			at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/IProfiler;endStartSection(Ljava/lang/String;)V", args = "ldc=cache"))
	private void onceDone(ModelBakery bakery, IResourceManager resourceManager, IProfiler profiler, CallbackInfo callback) {
		profiler.endStartSection("model_stats");

		profiler.startSection("unbaked");
		//ModelClasses.note(FMLPaths.GAMEDIR.get().resolve("unbaked_models.txt"), ((ModelBakeryAccess) bakery).getUnbakedModels().values()/*, true*/);
		profiler.endStartSection("baked");
		//ModelClasses.note(FMLPaths.GAMEDIR.get().resolve("baked_models.txt"), ((ModelBakeryAccess) bakery).getBakedModels().values(), true);
		Set<IBakedModel> modelsToWrite = new ReferenceOpenHashSet<>(bakery.getTopBakedModels().values());
		modelsToWrite.addAll(((ModelBakeryAccess) bakery).getBakedModels().values());
		ModelClasses.noteMain(FMLPaths.GAMEDIR.get().resolve("baked_top_models.txt"), modelsToWrite);
		profiler.endSection();

		profiler.endStartSection("model_serialisation");
		Path models = FMLPaths.GAMEDIR.get().resolve("serialised_baked_models.txt");
		boolean write = true;
		profiler.startSection("serialise");
		if (write) ModelSerialiser.serialise(((ModelBakeryAccess) bakery).getBakedModels(), bakery.getTopBakedModels(), models);
		profiler.endStartSection("optimise");
		if (write) ModelSerialiser.optimise(models);
		profiler.endStartSection("deserialise");
		Map<?, IBakedModel> remade = ModelSerialiser.deserialise(models);
		profiler.endStartSection("compare");
		ModelSerialiser.compare(remade);
		profiler.endSection();
	}
}