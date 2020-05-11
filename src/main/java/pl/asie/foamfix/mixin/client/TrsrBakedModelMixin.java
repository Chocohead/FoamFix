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

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Opcodes;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.IBakedModel;

import com.raoulvdberge.refinedpipes.render.TrsrBakedModel;

@Pseudo //Don't want to hard depend on Refined Pipes at runtime
@Mixin(value = TrsrBakedModel.class, remap = false)
abstract class TrsrBakedModelMixin implements IBakedModel {
	@Unique
	private static final Map<TransformationMatrix, TransformationMatrix> MATRIX_POOL = new HashMap<>();

	@Mutable
	@Shadow
	protected @Final TransformationMatrix transformation;

	@Redirect(method = {"<init>(Lnet/minecraft/client/renderer/model/IBakedModel;Lnet/minecraft/util/Direction;Lnet/minecraft/client/renderer/Vector3f;)V",
						"<init>(Lnet/minecraft/client/renderer/model/IBakedModel;Lnet/minecraft/client/renderer/TransformationMatrix;)V"},
				at = @At(value = "FIELD", 
							target = "Lcom/raoulvdberge/refinedpipes/render/TrsrBakedModel;transformation:Lnet/minecraft/client/renderer/TransformationMatrix;", 
							opcode = Opcodes.PUTFIELD))
	private void summeriseMatrix(TrsrBakedModel self, TransformationMatrix matrix) {
		matrix = matrix.blockCenterToCorner();
		transformation = MATRIX_POOL.computeIfAbsent(matrix, m -> new TransformationMatrix(m.getMatrix()));
	}

	@Redirect(method = "getQuads(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/Direction;Ljava/util/Random;)Ljava/util/List;",
				at = @At(value = "INVOKE", 
							target = "Lnet/minecraft/client/renderer/TransformationMatrix;blockCenterToCorner()Lnet/minecraft/client/renderer/TransformationMatrix;",
							remap = false),
				remap = true)
	private TransformationMatrix skipShift(TransformationMatrix self) {
		return self;
	}
}