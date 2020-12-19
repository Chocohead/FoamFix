/*
 * Copyright (C) 2020 Chocohead
 *
 * This file is part of FoamFix.
 *
 * FoamFix is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoamFix is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FoamFix.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or
 * combining it with the Minecraft game engine, the Mojang Launchwrapper,
 * the Mojang AuthLib and the Minecraft Realms library (and/or modified
 * versions of said software), containing parts covered by the terms of
 * their respective licenses, the licensors of this Program grant you
 * additional permission to convey the resulting work.
 */
package pl.asie.foamfix.multipart;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrays;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.model.VariantList;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.multipart.Multipart;
import net.minecraft.client.renderer.model.multipart.Selector;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

import pl.asie.foamfix.FoamyCacherCleanser;

public class ResolvedMultipart implements IUnbakedModel {
	@SuppressWarnings("unchecked") //Thanks for the rawtype FastUtil, very cool
	private static final Map<VariantList[], ResolvedMultipart> CACHE = new Object2ObjectOpenCustomHashMap<>(ObjectArrays.HASH_STRATEGY);
	static {
		FoamyCacherCleanser.addCleaner(() -> {//Once the instances are all made there is no need to remember them
			CACHE.clear();
			((Object2ObjectOpenCustomHashMap<?, ?>) CACHE).trim();
		});
	}

	public static ResolvedMultipart create(Multipart multipart, BlockState state) {
		StateContainer<Block, BlockState> container = state.getBlock().getStateContainer();
		VariantList[] variants = multipart.getSelectors().stream().filter(selector -> selector.getPredicate(container).test(state)).map(Selector::getVariantList).toArray(VariantList[]::new);
		return CACHE.computeIfAbsent(variants, ResolvedMultipart::new);
	}

	private final VariantList[] variants;

	private ResolvedMultipart(VariantList[] variants) {
		this.variants = variants;
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || (obj instanceof ResolvedMultipart && Arrays.equals(variants, ((ResolvedMultipart) obj).variants));
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(variants);
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Arrays.stream(variants).map(VariantList::getDependencies).flatMap(Collection::stream).collect(Collectors.toSet());
	}

	@Override
	public Collection<RenderMaterial> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
		return Arrays.stream(variants).flatMap(variant -> variant.getTextures(modelGetter, missingTextureErrors).stream()).collect(Collectors.toSet());
	}

	@Override
	public IBakedModel bakeModel(ModelBakery modelBakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform transform, ResourceLocation location) {
		return new ResolvedMultipartModel(Arrays.stream(variants).map(variant -> variant.bakeModel(modelBakery, spriteGetter, transform, location)).filter(Objects::nonNull).toArray(IBakedModel[]::new));
	}

	private static class ResolvedMultipartModel implements IDynamicBakedModel {
		private final IBakedModel[] models;

		ResolvedMultipartModel(IBakedModel[] models) {
			this.models = models;
		}

		@Override
		public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
			long seed = rand.nextLong();
			Random random = new Random(seed);

			return Arrays.stream(models).flatMap(model -> {
				random.setSeed(seed);
				return model.getQuads(state, side, random, extraData).stream();
			}).collect(Collectors.toList());
		}

		@Override
		public boolean isAmbientOcclusion() {
			return models[0].isAmbientOcclusion();
		}

		@Override
		public boolean isAmbientOcclusion(BlockState state) {
			return models[0].isAmbientOcclusion(state);
		}

		@Override
		public boolean isGui3d() {
			return models[0].isGui3d();
		}

		@Override
		public boolean isSideLit() {
			return models[0].isSideLit();
		}

		@Override
		public boolean isBuiltInRenderer() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return getParticleTexture(EmptyModelData.INSTANCE);
		}

		@Override
		public TextureAtlasSprite getParticleTexture(IModelData data) {
			return models[0].getParticleTexture(data);
		}

		@Override
		@Deprecated
		public ItemCameraTransforms getItemCameraTransforms() {
			return models[0].getItemCameraTransforms();
		}

		@Override
		public boolean doesHandlePerspectives() {
			return models[0].doesHandlePerspectives();
		}

		@Override
		public IBakedModel handlePerspective(TransformType cameraTransformType, MatrixStack matices) {
			return models[0].handlePerspective(cameraTransformType, matices);
		}

		@Override
		public ItemOverrideList getOverrides() {
			return models[0].getOverrides();
		}
	}
}