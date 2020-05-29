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
import java.util.BitSet;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import org.objectweb.asm.Opcodes;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.MultipartBakedModel;
import net.minecraft.util.Direction;

import pl.asie.foamfix.multipart.FoamyAnyPredicate;

@Mixin(MultipartBakedModel.class)
abstract class MixinMultipartBakedModel implements IBakedModel {
	@Shadow
	@Mutable //We're only changing it once in it's own constructor call but Mixin insists it is a good idea anyway
	private @Final List<Pair<Predicate<BlockState>, IBakedModel>> selectors;
	@Shadow
	private @Final Map<BlockState, BitSet> field_210277_g;
	@Unique
	private @Final Predicate<BlockState>[] componentTests;
	@Unique
	private @Final IBakedModel[] componentModels;

	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/model/MultipartBakedModel;selectors:Ljava/util/List;", opcode = Opcodes.PUTFIELD))
	private void noNeed(MultipartBakedModel self, List<Pair<Predicate<BlockState>, IBakedModel>> components) {
		selectors = Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	@Inject(method = "<init>", at = @At("RETURN"))
	private void construct(List<Pair<Predicate<BlockState>, IBakedModel>> components, CallbackInfo info) {
		Map<IBakedModel, List<Predicate<BlockState>>> map = components.stream().collect(Collectors.groupingBy(Pair::getRight, IdentityHashMap::new, Collectors.mapping(Pair::getLeft, Collectors.toList())));

		int size = map.size();
		componentTests = new Predicate[size];
		componentModels = new IBakedModel[size];

		if (size == components.size()) {//Is there some value into trying to flatten the state predicates out?
			for (int i = 0; i < size; i++) {
				Pair<Predicate<BlockState>, IBakedModel> component = components.get(i);
				componentTests[i] = component.getLeft();
				componentModels[i] = component.getRight();
			}
		} else {
			int i = 0;
			for (Entry<IBakedModel, List<Predicate<BlockState>>> entry : map.entrySet()) {
				componentTests[i] = FoamyAnyPredicate.ofFlattened(entry.getValue());
				componentModels[i++] = entry.getKey();
			}
		}
	}

	/**
	 * @author Chocohead
	 * @reason Use direct arrays over a list of pairs
	 */
	@Override
	@Overwrite
	@SuppressWarnings("deprecation")
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
		if (state != null) {
			BitSet activeParts = field_210277_g.get(state);
			if (activeParts == null) {
				activeParts = new BitSet();

				for (int i = componentTests.length - 1; i >= 0; i--) {
					if (componentTests[i].test(state)) {
						activeParts.set(i);
					}
				}

				field_210277_g.put(state, activeParts);
			}

			if (!activeParts.isEmpty()) {
				List<BakedQuad> quads = new ArrayList<>();

				long seed = rand.nextLong();
				Random componentRand = new Random(seed);

				for (int i = activeParts.nextSetBit(0); i >= 0; i = activeParts.nextSetBit(i + 1)) {
					componentRand.setSeed(seed);
					quads.addAll(componentModels[i].getQuads(state, side, componentRand));
				}

				return quads;
			}
		}

		return Collections.emptyList();
	}
}