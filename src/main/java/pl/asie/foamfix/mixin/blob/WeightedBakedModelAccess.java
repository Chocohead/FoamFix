package pl.asie.foamfix.mixin.blob;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.renderer.model.WeightedBakedModel;
import net.minecraft.client.renderer.model.WeightedBakedModel.WeightedModel;

@Mixin(WeightedBakedModel.class)
public interface WeightedBakedModelAccess {
	@Accessor
	List<WeightedModel> getModels();
}