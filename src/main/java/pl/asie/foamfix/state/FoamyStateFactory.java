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
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import com.mojang.serialization.MapCodec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.StateHolder;

import pl.asie.foamfix.FoamyCacherCleanser;
import pl.asie.foamfix.Util;

public class FoamyStateFactory<O, S extends StateHolder<O, S>> extends StateContainer<O, S> {
	private static final Map<ImmutableMap<Property<?>, Comparable<?>>, ImmutableMap<Property<?>, Comparable<?>>> CACHE = new Object2ObjectOpenHashMap<>();
	static {
		FoamyCacherCleanser.addCleaner(() -> {
			CACHE.clear();
			((Object2ObjectOpenHashMap<?, ?>) CACHE).trim();
		});
	}

	public FoamyStateFactory(Function<O, S> ownerToState, O baseObject, IFactory<O, S> factory, Map<String, Property<?>> map) {
		super(ownerToState, baseObject, wrapFactory(getFactory(baseObject, factory)), map);
	}

	private static <O, S extends StateHolder<O, S>> IFactory<O, S> wrapFactory(IFactory<O, S> factory) {
		return (owner, properties, codec) -> {
			//This can and will be called concurrently which can make for strange crashes rehashing
			return factory.create(owner, Util.syncIfAbsent(CACHE, properties, Function.identity()), codec);
		};
	}

	public static boolean hasFactory(Object baseObject) {
		return baseObject instanceof Block;
	}

	@SuppressWarnings("unchecked")
	private static <O, S extends StateHolder<O, S>> IFactory<O, S> getFactory(O baseObject, IFactory<O, S> fallback) {
		if (baseObject instanceof Block) {
			return (Factory<O, S>) new Factory<Block, BlockState>(FoamyBlockStateMapped::new, FoamyBlockStateEmpty::new);
		} else {
			System.err.println("[FoamFix/FoamyStateFactory] Should not be here! Is hasFactory matching getFactory? " + baseObject.getClass().getName());
			return fallback;
		}
	}

	private interface MappedStateFactory<O, S extends StateHolder<O, S>> {
		S create(PropertyValueMapper<S> mapper, O baseObject, ImmutableMap<Property<?>, Comparable<?>> map, MapCodec<S> codec);
	}

	private static class Factory<O, S extends StateHolder<O, S>> implements IFactory<O, S> {
		private final MappedStateFactory<O, S> factory;
		private final BiFunction<O, MapCodec<S>, S> emptyFactory;
		private PropertyValueMapper<S> mapper;

		public Factory(MappedStateFactory<O, S> factory, BiFunction<O, MapCodec<S>, S> emptyFactory) {
			this.factory = factory;
			this.emptyFactory = emptyFactory;
		}

		@Override
		public S create(O owner, ImmutableMap<Property<?>, Comparable<?>> properties, MapCodec<S> codec) {
			if (properties.isEmpty()) {
				return emptyFactory.apply(owner, codec);
			}

			if (mapper == null) {
				mapper = properties.size() == 1 ? new SinglePropertyValueMapper<>(Iterables.getOnlyElement(properties.keySet())) : new PropertyValueMapperImpl<>(properties.keySet());
			}

			return factory.create(mapper, owner, properties, codec);
		}
	}
}
