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

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IProperty;

public class FoamyBlockStateMapped extends BlockState {
	protected final PropertyValueMapper<BlockState> owner;
	protected int value;

	public FoamyBlockStateMapped(PropertyValueMapper<BlockState> owner, Block blockIn, ImmutableMap<IProperty<?>, Comparable<?>> propertiesIn) {
		super(blockIn, propertiesIn);
		this.owner = owner;
	}

	@Override
	public <T extends Comparable<T>, V extends T> BlockState with(IProperty<T> property, V value) {
		BlockState state = owner.with(this.value, property, value);

		if (state == null) {
			Comparable<?> comparable = getValues().get(property);
			if (comparable == null) {
				throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + this.getBlock());
			} else {
				throw new IllegalArgumentException("Cannot set property " + property + " to " + value + " on block " + this.getBlock() + ", it is not an allowed value");
			}
		} else {
			return state;
		}
	}

	@Override
	public void buildPropertyValueTable(Map<Map<IProperty<?>, Comparable<?>>, BlockState> map_1) {
		this.value = owner.generateValue(this);
	}
}
