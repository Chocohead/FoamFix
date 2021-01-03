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
package pl.asie.foamfix.mixin.dfu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

import net.minecraft.util.datafix.DataFixesManager;

import pl.asie.foamfix.FoamyConfig;
import pl.asie.foamfix.dfu.NoopSchema;

@Mixin(DataFixesManager.class)
class SchemasMixin {
	/**
	 * @author Chocohead
	 * @reason DFU is huge and slow, so if it isn't needed we save a lot
	 */
	@Overwrite
	private static DataFixer createFixer() {
		return new DataFixer() {
			private final Logger logger = LogManager.getLogger("DataBreakerLower");
			private final Schema noopSchema = new NoopSchema();

			@Override
			public <T> Dynamic<T> update(TypeReference type, Dynamic<T> input, int version, int newVersion) {
				if (version < newVersion) {
					//This is probably not good for the thing which needs to be updated
					if (FoamyConfig.LOG_DFU.asBoolean()) logger.warn("Skipping updating a " + type + " (" + input + ") from " + version);
				}

				return input;
			}

			@Override
			public Schema getSchema(int key) {
				return noopSchema;
			}
		};
	}
}