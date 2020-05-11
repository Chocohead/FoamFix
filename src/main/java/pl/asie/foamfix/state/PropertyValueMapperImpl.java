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
import java.util.List;

import com.google.common.collect.Lists;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import net.minecraft.state.IProperty;
import net.minecraft.state.IStateHolder;

public class PropertyValueMapperImpl<C extends IStateHolder<C>> implements PropertyValueMapper<C> {
	private static final Comparator<? super IProperty<?>> COMPARATOR_BIT_FITNESS = (Comparator<IProperty<?>>) (first, second) -> {
		int diff1 = PropertyOrdering.getEntry(first).bitSize - first.getAllowedValues().size();
		int diff2 = PropertyOrdering.getEntry(second).bitSize - second.getAllowedValues().size();
		// We want to put properties with higher diff-values last,
		// so that the array is as small as possible.
		if (diff1 == diff2) {
			return first.getName().compareTo(second.getName());
		} else {
			return diff1 - diff2;
		}
	};

	private final PropertyOrdering.Entry[] entryList;
	private final Object2IntOpenHashMap<String> entryPositionMap;
	private final C[] stateMap;

	@SuppressWarnings("unchecked") //Close enough given the bounds of C
	public PropertyValueMapperImpl(Collection<IProperty<?>> properties) {
		entryList = new PropertyOrdering.Entry[properties.size()];
		List<IProperty<?>> propertiesSortedFitness = Lists.newArrayList(properties);
		propertiesSortedFitness.sort(COMPARATOR_BIT_FITNESS);
		int i = 0;
		for (IProperty<?> p : propertiesSortedFitness) {
			entryList[i++] = PropertyOrdering.getEntry(p);
		}

		entryPositionMap = new Object2IntOpenHashMap<>(properties.size());
		entryPositionMap.defaultReturnValue(-1);

		int bitPos = 0;
		PropertyOrdering.Entry lastEntry = null;
		for (PropertyOrdering.Entry ee : entryList) {
			entryPositionMap.put(ee.property.getName(), bitPos);
			bitPos += ee.bits;
			lastEntry = ee;
		}

		if (lastEntry == null) {
			stateMap = (C[]) new IStateHolder[1 << bitPos];
		} else {
			stateMap = (C[]) new IStateHolder[(1 << (bitPos - lastEntry.bits)) * lastEntry.property.getAllowedValues().size()];
		}
	}

	public int generateValue(C state) {
		int bitPos = 0;
		int value = 0;
		for (PropertyOrdering.Entry e : entryList) {
			value |= e.get(state.get(e.property)) << bitPos;
			bitPos += e.bits;
		}

		stateMap[value] = state;
		return value;
	}

	public <T extends Comparable<T>, V extends T> C with(int value, IProperty<T> property, V propertyValue) {
		int bitPos = entryPositionMap.getInt(property.getName());
		if (bitPos >= 0) {
			PropertyOrdering.Entry e = PropertyOrdering.getEntry(property);
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

	public <T extends Comparable<T>, V extends T> int withValue(int value, IProperty<T> property, V propertyValue) {
		int bitPos = entryPositionMap.getInt(property.getName());
		if (bitPos >= 0) {
			PropertyOrdering.Entry e = PropertyOrdering.getEntry(property);
			int nv = e.get(propertyValue);
			if (nv < 0) return -1;

			int bitMask = (e.bitSize - 1);
			value = (value & (~(bitMask << bitPos)) | (nv << bitPos));

			return value;
		}

		return -1;
	}

}
