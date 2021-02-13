package pl.asie.foamfix.mixin.blob;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.google.gson.JsonElement;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IUnbakedModel;

import team.chisel.ctm.client.model.ModelCTM;

@Pseudo
@Mixin(value = ModelCTM.class, remap = false)
public interface ModelCTMAccess {
	@Accessor
	IUnbakedModel getVanillamodel();

	@Accessor
	BlockModel getModelinfo();

	@Accessor
	Int2ObjectMap<JsonElement> getOverrides();
}