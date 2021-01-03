/*
 * Copyright (C) 2020, 2021 Chocohead
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
package pl.asie.foamfix.mixin.multipart.best;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.mojang.datafixers.util.Pair;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelBakery.ModelListWrapper;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.model.multipart.Multipart;
import net.minecraft.util.ResourceLocation;

import pl.asie.foamfix.multipart.ResolvedMultipart;

@Mixin(ModelBakery.class)
abstract class ModelBakeryMixin {
	@ModifyVariable(method = "lambda$loadBlockstate$25", at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/util/Pair;getFirst()Ljava/lang/Object;", shift = Shift.BY, by = -2, remap = false), ordinal = 1)
	private Pair<IUnbakedModel, Supplier<ModelListWrapper>> wrapMultipartModels(Pair<IUnbakedModel, Supplier<ModelListWrapper>> variant, Map<BlockState, Pair<IUnbakedModel, Supplier<ModelListWrapper>>> stateToModel, 
			ResourceLocation blockModel, Pair<IUnbakedModel, Supplier<ModelListWrapper>> missingModel, HashMap<?, Set<BlockState>> wrappedModelToState, ModelResourceLocation stateModel, BlockState state) {
		IUnbakedModel model = variant.getFirst();
		if (!(model instanceof Multipart)) return variant;

		//System.out.println("Multipart for " + stateModel);
		IUnbakedModel resolvedModel = ResolvedMultipart.create((Multipart) model, state);

		return Pair.of(resolvedModel, () -> new ModelListWrapper(Collections.singletonList(resolvedModel), ((ModelListWrapperAccess) variant.getSecond().get()).getColorValues()));
	}
}