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

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.collect.Streams;

import net.minecraft.block.BlockState;

public final class FoamyAnyPredicate implements Predicate<BlockState> {
	private final Predicate<BlockState>[] predicates;
	private final boolean positive;

	public FoamyAnyPredicate(boolean positive, Predicate<BlockState>[] predicates) {
		this.predicates = predicates;
		this.positive = positive;
	}

	@Override
	public boolean test(BlockState state) {
		for (Predicate<BlockState> predicate : predicates) {
			if (predicate.test(state)) {
				return positive;
			}
		}

		return !positive;
	}

	@Override
	public Predicate<BlockState> negate() {
		return new FoamyAnyPredicate(!positive, predicates);
	}

	public Stream<Predicate<BlockState>> flatten() {
		return Arrays.stream(predicates).flatMap(test -> {
			if (test.getClass() == FoamyAnyPredicate.class && ((FoamyAnyPredicate) test).positive == positive) {
				return ((FoamyAnyPredicate) test).flatten();
			} else {
				return Stream.of(test);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public static Predicate<BlockState> ofFlattened(Iterable<Predicate<BlockState>> tests) {
		return new FoamyAnyPredicate(true, Streams.stream(tests).flatMap(test -> {
			if (test.getClass() == FoamyAnyPredicate.class && ((FoamyAnyPredicate) test).positive) {
				return ((FoamyAnyPredicate) test).flatten();
			} else {
				return Stream.of(test);
			}
		}).toArray(Predicate[]::new));
	}
}