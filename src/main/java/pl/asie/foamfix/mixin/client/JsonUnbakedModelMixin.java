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
package pl.asie.foamfix.mixin.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.datafixers.util.Either;

import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.BlockModel.GuiLight;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverride;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.util.ResourceLocation;

@Mixin(BlockModel.class)
abstract class JsonUnbakedModelMixin {
	@Inject(method = "<init>", at = @At("RETURN"))
	private void shrink(ResourceLocation parentId, List<BlockPart> elements, Map<String, Either<RenderMaterial, String>> textureMap, boolean ambientOcclusion,
						GuiLight guiLight, ItemCameraTransforms transformations, List<ItemOverride> overrides, CallbackInfo info) {
		if (elements instanceof ArrayList) {
			((ArrayList<?>) elements).trimToSize();
		}

		if (overrides instanceof ArrayList) {
			((ArrayList<?>) overrides).trimToSize();
		}
	}
}