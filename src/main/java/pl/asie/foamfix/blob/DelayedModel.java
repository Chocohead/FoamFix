package pl.asie.foamfix.blob;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;

class DelayedModel implements IBakedModel {
	public final Object real;

	DelayedModel(Object real) {
		this.real = real;
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
		throw new AssertionError("Unexpected code path");
	}

	@Override
	public boolean isAmbientOcclusion() {
		throw new AssertionError("Unexpected code path");
	}

	@Override
	public boolean isGui3d() {
		throw new AssertionError("Unexpected code path");
	}

	@Override
	public boolean isSideLit() {
		throw new AssertionError("Unexpected code path");
	}

	@Override
	public boolean isBuiltInRenderer() {
		throw new AssertionError("Unexpected code path");
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		throw new AssertionError("Unexpected code path");
	}

	@Override
	public ItemOverrideList getOverrides() {
		throw new AssertionError("Unexpected code path");
	}
}