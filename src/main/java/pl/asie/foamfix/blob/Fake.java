package pl.asie.foamfix.blob;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import com.mojang.datafixers.util.Pair;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.model.IModelConfiguration;

import team.chisel.ctm.api.model.IModelCTM;
import team.chisel.ctm.api.texture.ICTMTexture;

final class Fake implements IBakedModel, IModelCTM {
	public static final Fake INSTANCE = new Fake();

	private Fake() {
	}

	@Override
	public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery,
			Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform,
			ItemOverrideList overrides, ResourceLocation modelLocation) {
		throw new UnsupportedOperationException("Unexpectedly reached code path");
	}

	@Override
	public Collection<RenderMaterial> getTextures(IModelConfiguration owner,
			Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
		throw new UnsupportedOperationException("Unexpectedly reached code path");
	}

	@Override
	public boolean canRenderInLayer(BlockState state, RenderType layer) {
		throw new UnsupportedOperationException("Unexpectedly reached code path");
	}

	@Override
	public Collection<ICTMTexture<?>> getCTMTextures() {
		throw new UnsupportedOperationException("Unexpectedly reached code path");
	}

	@Override
	public TextureAtlasSprite getOverrideSprite(int tintIndex) {
		throw new UnsupportedOperationException("Unexpectedly reached code path");
	}

	@Override
	public ICTMTexture<?> getOverrideTexture(int tintIndex, ResourceLocation location) {
		throw new UnsupportedOperationException("Unexpectedly reached code path");

	}

	@Override
	public ICTMTexture<?> getTexture(ResourceLocation location) {
		throw new UnsupportedOperationException("Unexpectedly reached code path");
	}

	@Override
	public void load() {
		throw new UnsupportedOperationException("Unexpectedly reached code path");
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
		throw new UnsupportedOperationException("Unexpectedly reached code path");
	}

	@Override
	public boolean isAmbientOcclusion() {
		throw new UnsupportedOperationException("Unexpectedly reached code path");
	}

	@Override
	public boolean isGui3d() {
		throw new UnsupportedOperationException("Unexpectedly reached code path");
	}

	@Override
	public boolean isSideLit() {
		throw new UnsupportedOperationException("Unexpectedly reached code path");
	}

	@Override
	public boolean isBuiltInRenderer() {
		throw new UnsupportedOperationException("Unexpectedly reached code path");
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		throw new UnsupportedOperationException("Unexpectedly reached code path");
	}

	@Override
	public ItemOverrideList getOverrides() {
		throw new UnsupportedOperationException("Unexpectedly reached code path");
	}
}