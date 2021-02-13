package pl.asie.foamfix.mixin.blob;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;

@Mixin(ItemOverrideList.class)
public interface ItemOverrideListAccess {
	@Accessor
	List<IBakedModel> getOverrideBakedModels();
}