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

import java.util.Collection;
import java.util.Comparator;

import it.unimi.dsi.fastutil.objects.Reference2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2IntSortedMap;
import it.unimi.dsi.fastutil.objects.Reference2IntSortedMaps;

import net.minecraft.state.Property;
import net.minecraft.state.StateHolder;

import pl.asie.foamfix.state.PropertyOrdering.Entry;

public class PropertyValueMapperImpl<C extends StateHolder<?, C>> implements PropertyValueMapper<C> {
	private static final Comparator<? super Entry> COMPARATOR_BIT_FITNESS = (Entry first, Entry second) -> {
		int diff1 = first.bitSize - first.property.getAllowedValues().size();
		int diff2 = second.bitSize - second.property.getAllowedValues().size();
		// We want to put properties with higher diff-values last,
		// so that the array is as small as possible.
		if (diff1 == diff2) {
			return first.property.getName().compareTo(second.property.getName());
		} else {
			return diff1 - diff2;
		}
	};

	private final Reference2IntSortedMap<Entry> entryPositionMap;
	private final C[] stateMap;

	@SuppressWarnings("unchecked") //Close enough given the bounds of C
	public PropertyValueMapperImpl(Collection<Property<?>> properties) {
		Reference2IntLinkedOpenHashMap<Entry> entryPositionMap = new Reference2IntLinkedOpenHashMap<>(properties.size(), 1);
		entryPositionMap.defaultReturnValue(-1);

		int bitPos = 0;
		for (Entry ee : (Iterable<Entry>) properties.stream().map(PropertyOrdering::getEntry).sorted(COMPARATOR_BIT_FITNESS)::iterator) {
			entryPositionMap.put(ee, bitPos);
			bitPos += ee.bits;
		}

		entryPositionMap.trim();
		this.entryPositionMap = Reference2IntSortedMaps.unmodifiable(entryPositionMap);

		Entry lastEntry = entryPositionMap.lastKey();
		stateMap = (C[]) new StateHolder[(1 << (bitPos - lastEntry.bits)) * lastEntry.property.getAllowedValues().size()];
	}

	public int generateValue(C state) {
		int bitPos = 0;
		int value = 0;
		for (Entry e : entryPositionMap.keySet()) {
			value |= e.get(state.get(e.property)) << bitPos;
			bitPos += e.bits;
		}

		stateMap[value] = state;
		return value;
	}

	public <T extends Comparable<T>, V extends T> C with(int value, Property<T> property, V propertyValue) {
		Entry e = PropertyOrdering.getEntry(property);

		int bitPos = entryPositionMap.getInt(e);
		if (bitPos >= 0) {
			int nv = e.get(propertyValue);
			if (nv < 0) return null;

			int bitMask = (e.bitSize - 1);
			value = (value & (~(bitMask << bitPos)) | (nv << bitPos));

			return stateMap[value];
		}

		return null;
	}

	public C getPropertyByValue(int value) {
		return stateMap[value];
	}

	public <T extends Comparable<T>, V extends T> int withValue(int value, Property<T> property, V propertyValue) {
		Entry e = PropertyOrdering.getEntry(property);

		int bitPos = entryPositionMap.getInt(e);
		if (bitPos >= 0) {
			int nv = e.get(propertyValue);
			if (nv < 0) return -1;

			int bitMask = (e.bitSize - 1);
			value = (value & (~(bitMask << bitPos)) | (nv << bitPos));

			return value;
		}

		return -1;
	}
}
