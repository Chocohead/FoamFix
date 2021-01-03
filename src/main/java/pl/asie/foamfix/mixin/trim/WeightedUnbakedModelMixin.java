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
package pl.asie.foamfix.mixin.trim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Iterables;

import net.minecraft.client.renderer.model.Variant;
import net.minecraft.client.renderer.model.VariantList;
import net.minecraft.client.renderer.model.VariantList.Deserializer;

@Mixin(VariantList.class)
abstract class WeightedUnbakedModelMixin {
	@Inject(method = "<init>", at = @At("RETURN"))
	private void shrink(List<Variant> variants, CallbackInfo info) {
		if (variants instanceof ArrayList) {
			((ArrayList<?>) variants).trimToSize();
		}
	}

	@Mixin(Deserializer.class)
	static abstract class DeserialiserMixin {
		@ModifyArg(method = "deserialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/model/VariantList;<init>(Ljava/util/List;)V", ordinal = 0))
		private List<Variant> shrink(List<Variant> variants) {
			switch (variants.size()) {
			case 0:
				return Collections.emptyList();

			case 1:
				return Collections.singletonList(Iterables.getOnlyElement(variants));

			default:
				return Arrays.asList(variants.toArray(new Variant[0]));
			}
		}
	}
}