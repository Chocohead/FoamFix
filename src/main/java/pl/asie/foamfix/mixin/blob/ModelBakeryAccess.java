package pl.asie.foamfix.mixin.blob;

import java.util.Map;

import org.apache.commons.lang3.tuple.Triple;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.SpriteMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.TransformationMatrix;

@Mixin(ModelBakery.class)
public interface ModelBakeryAccess {
	@Accessor
	SpriteMap getSpriteMap();

	@Accessor
	Map<ResourceLocation, IUnbakedModel> getUnbakedModels();

	@Accessor
	Map<Triple<ResourceLocation, TransformationMatrix, Boolean>, IBakedModel> getBakedModels();
}