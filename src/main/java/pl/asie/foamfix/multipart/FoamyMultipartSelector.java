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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import net.minecraft.block.BlockState;
import net.minecraft.state.IProperty;

public class FoamyMultipartSelector implements Predicate<BlockState> {
	private static final Map<FoamyMultipartSelector, FoamyMultipartSelector> CACHE = new HashMap<>();

	public static FoamyMultipartSelector create(IProperty<?> property, Object value) {
		return CACHE.computeIfAbsent(new FoamyMultipartSelector(property, value), Function.identity());
	}

	private final IProperty<?> property;
	private final Object value;
	private final boolean positive;

	private FoamyMultipartSelector(IProperty<?> property, Object value) {
		this(property, value, true);
	}

	private FoamyMultipartSelector(IProperty<?> property, Object value, boolean positive) {
		this.property = property;
		this.value = value;
		this.positive = positive;
	}

	@Override
	public boolean test(BlockState state) {
		return state.get(property).equals(value) == positive;
	}

	@Override
	public FoamyMultipartSelector negate() {
		return CACHE.computeIfAbsent(new FoamyMultipartSelector(property, value, !positive), Function.identity());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;

		if (obj instanceof FoamyMultipartSelector) {
			FoamyMultipartSelector that = (FoamyMultipartSelector) obj;
			return property.equals(that.property) && value.equals(that.value) && positive == that.positive;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hash = property.hashCode() ^ value.hashCode();
		return positive ? hash : ~hash;
	}

	@Override
	public String toString() {
		return "(" + property + ',' + value + ')';
	}
}