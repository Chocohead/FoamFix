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

package pl.asie.foamfix.state;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.state.IProperty;
import net.minecraft.state.IStateHolder;
import net.minecraft.state.StateContainer;
import net.minecraft.state.StateHolder;

public class FoamyStateFactory<O, S extends IStateHolder<S>> extends StateContainer<O, S> {
	public <A extends StateHolder<O, S>> FoamyStateFactory(O baseObject, IFactory<O, S, A> factory, Map<String, IProperty<?>> map) {
		super(baseObject, getFactory(baseObject, factory), map);
	}

	public static boolean hasFactory(Object baseObject) {
		return baseObject instanceof Block || baseObject instanceof Fluid;
	}

	@SuppressWarnings("unchecked")
	private static <O, S extends IStateHolder<S>, A extends StateHolder<O, S>> IFactory<O, S, A> getFactory(O baseObject, IFactory<O, S, A> fallback) {
		if (baseObject instanceof Block) {
			return (Factory<O, S, A>) new Factory<Block, BlockState, BlockState>(FoamyBlockStateMapped::new, FoamyBlockStateEmpty::new);
		} else if (baseObject instanceof Fluid) {
			return (Factory<O, S, A>) new Factory<Fluid, IFluidState, FluidState>(FoamyFluidStateMapped::new, FoamyFluidStateEmpty::new);
		} else {
			System.err.println("[FoamFix/FoamyStateFactory] Should not be here! Is hasFactory matching getFactory? " + baseObject.getClass().getName());
			return fallback;
		}
	}

	private interface MappedStateFactory<O, S extends IStateHolder<S>, A extends StateHolder<O, S>> {
		A create(PropertyValueMapper<S> mapper, O baseObject, ImmutableMap<IProperty<?>, Comparable<?>> map);
	}

	private static class Factory<O, S extends IStateHolder<S>, A extends StateHolder<O, S>> implements IFactory<O, S, A> {
		private final MappedStateFactory<O, S, A> factory;
		private final Function<O, A> emptyFactory;
		private PropertyValueMapper<S> mapper;

		public Factory(MappedStateFactory<O, S, A> factory, Function<O, A> emptyFactory) {
			this.factory = factory;
			this.emptyFactory = emptyFactory;
		}

		@Override
		public A create(O var1, ImmutableMap<IProperty<?>, Comparable<?>> var2) {
			if (var2.isEmpty()) {
				return emptyFactory.apply(var1);
			}

			if (mapper == null) {
				mapper = var2.size() == 1 ? new SinglePropertyValueMapper<>(Iterables.getOnlyElement(var2.keySet())) : new PropertyValueMapperImpl<>(var2.keySet());
			}

			return factory.create(mapper, var1, var2);
		}
	}
}
