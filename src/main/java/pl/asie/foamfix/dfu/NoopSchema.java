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
package pl.asie.foamfix.dfu;

import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;

import net.minecraft.util.SharedConstants;

public class NoopSchema extends Schema {
	public NoopSchema() {
		super(SharedConstants.getVersion().getWorldVersion(), null);
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		return Collections.emptyMap();
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		return Collections.emptyMap();
	}

	@Override
	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
	}
	
	@Override
	protected Map<String, Type<?>> buildTypes() {
		return Collections.emptyMap();
	}

	@Override
	public Type<?> getTypeRaw(TypeReference type) {
		throw new UnsupportedOperationException("Tried to get raw type of " + type);
	}

	@Override
	public Type<?> getType(TypeReference type) {
		throw new UnsupportedOperationException("Tried to get type of " + type);
	}

	@Override
	public TypeTemplate resolveTemplate(String name) {
		throw new UnsupportedOperationException("Tried to resolve template of " + name);
	}

	@Override
	public TaggedChoiceType<?> findChoiceType(TypeReference type) {
		return null;
	}

	@Override
	public Type<?> getChoiceType(TypeReference type, String choiceName) {
		return null;
	}
}