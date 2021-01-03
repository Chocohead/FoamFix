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
package pl.asie.foamfix.mixin.multipart.pool;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.multipart.PropertyValueCondition;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;

import pl.asie.foamfix.multipart.FoamyAnyPredicate;
import pl.asie.foamfix.multipart.FoamyMultipartSelector;

@Mixin(PropertyValueCondition.class)
abstract class SimpleMultipartModelSelectorMixin {
	@SuppressWarnings("unchecked")
	@Inject(method = "getPredicate", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", remap = false), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void predicateBetter(StateContainer<Block, BlockState> stateManager, CallbackInfoReturnable<Predicate<BlockState>> callback,
									Property<?> property, String value, boolean negate, List<String> values) {
		if (values.size() == 1) {
			Predicate<BlockState> out = makePropertyPredicate(stateManager, property, value);
			callback.setReturnValue(negate ? out.negate() : out);
		} else {
			callback.setReturnValue(new FoamyAnyPredicate(!negate, values.stream().map(v -> makePropertyPredicate(stateManager, property, v)).toArray(Predicate[]::new)));
		}
	}

	@Shadow //Yarn named as createPredicate
	abstract Predicate<BlockState> makePropertyPredicate(StateContainer<Block, BlockState> stateFactory, Property<?> property, String valueString);

	@Inject(method = "makePropertyPredicate", at = @At(value = "RETURN"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void cachePredicates(StateContainer<Block, BlockState> stateFactory, Property<?> property, String valueString, CallbackInfoReturnable<Predicate<BlockState>> callback, Optional<?> value) {
		callback.setReturnValue(FoamyMultipartSelector.create(property, value.get()));
	}
}