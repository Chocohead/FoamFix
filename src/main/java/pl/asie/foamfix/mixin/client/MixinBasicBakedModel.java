/*
 * Copyright (C) 2016, 2017, 2018, 2019 Adrian Siekierka
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

package pl.asie.foamfix.mixin.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Surrogate;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.SimpleBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;

@SuppressWarnings("deprecation") //Can't do anything about that
@Mixin(SimpleBakedModel.class)
public class MixinBasicBakedModel {
	/**
	 * This saves a good 9*7*8=504 bytes per model, in the best case, which isn't bad at all - and it doesn't hurt!
	 */
	@Inject(method = "<init>", at = @At("RETURN"))
	public void construct(List<BakedQuad> quads, Map<Direction, List<BakedQuad>> faceQuads, boolean usesAo, boolean isSideLit, boolean hasDepth, TextureAtlasSprite sprite, ItemCameraTransforms modelTransformation, ItemOverrideList modelItemPropertyOverrideList, CallbackInfo info) {
		construct(quads, faceQuads, usesAo, hasDepth, sprite, modelTransformation, modelItemPropertyOverrideList, info);
	}

	@Surrogate
	private void construct(List<BakedQuad> quads, Map<Direction, List<BakedQuad>> faceQuads, boolean usesAo, boolean is3dInGui, TextureAtlasSprite sprite, ItemCameraTransforms transformation, ItemOverrideList itemPropertyOverrides, CallbackInfo info) {
		if (quads instanceof ArrayList) {
			((ArrayList<BakedQuad>) quads).trimToSize();
		}

		for (List<BakedQuad> l : faceQuads.values()) {
			if (l instanceof ArrayList) {
				((ArrayList<BakedQuad>) l).trimToSize();
			}
		}
	}
}