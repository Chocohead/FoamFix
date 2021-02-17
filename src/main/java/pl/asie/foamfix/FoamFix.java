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
package pl.asie.foamfix;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;

import team.chisel.ctm.client.util.TextureMetadataHandler;

import pl.asie.foamfix.blob.CacheController;

@Mod("foamfix") //Placate Forge thinking we're a real mod too
@EventBusSubscriber(bus = Bus.MOD, value = Dist.CLIENT)
public class FoamFix {
	@SubscribeEvent
	public static void preInit(FMLClientSetupEvent event) {
		ModList.get().getModContainerById("ctm").ifPresent(ctm -> {
			if (ctm instanceof FMLModContainer) {
				if (CacheController.hasCache()) {
					IEventBus bus = ((FMLModContainer) ctm).getEventBus();
					//Remove the CTM wrapper if we're using the cache
					bus.unregister(TextureMetadataHandler.INSTANCE);
					bus.addListener(TextureMetadataHandler.INSTANCE::onTextureStitch);
				}
			} else {
				System.err.println("Unexpected mod type: " + ctm.getClass());
			}
		});
	}
}