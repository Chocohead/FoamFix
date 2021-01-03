/*
 * Copyright (C) 2020, 2021 Chocohead
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
package pl.asie.foamfix.mixin.memory;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IRegistryDelegate;

@Mixin(ModelBakery.class)
abstract class ModelBakeryMixin {
	@Redirect(method = "processLoading",
			at = @At(value = "NEW", target = "(Lnet/minecraft/util/ResourceLocation;Ljava/lang/String;)Lnet/minecraft/client/renderer/model/ModelResourceLocation;"),
			slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=items"),
							to = @At(value = "CONSTANT", args = "stringValue=special")))
	private ModelResourceLocation reuseModelLocation(ResourceLocation item, String inventory) {
		assert "inventory".equals(inventory);
		Map<IRegistryDelegate<Item>, ModelResourceLocation> itemToModel = ((ItemModelMesherForgeAccess) Minecraft.getInstance().getItemRenderer().getItemModelMesher()).getLocations();
	
		ModelResourceLocation out = itemToModel.get(ForgeRegistries.ITEMS.getValue(item).delegate);
		return out != null ? out : new ModelResourceLocation(item, inventory);
	}
}