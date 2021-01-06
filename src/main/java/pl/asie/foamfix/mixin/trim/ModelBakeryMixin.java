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
package pl.asie.foamfix.mixin.trim;

import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;

import org.objectweb.asm.Opcodes;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.datafixers.util.Pair;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.AtlasTexture.SheetData;
import net.minecraft.client.renderer.texture.SpriteMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.TransformationMatrix;

import pl.asie.foamfix.mixin.NoCast;
import pl.asie.foamfix.thready.ModelKey;

@Mixin(ModelBakery.class)
class ModelBakeryMixin {
	@Shadow
	@Mutable
	private @Final Map<ResourceLocation, IUnbakedModel> topUnbakedModels;
	@Shadow
	private Map<ResourceLocation, Pair<AtlasTexture, SheetData>> sheetData;

	@NoCast
	@Redirect(method = "<init>(Lnet/minecraft/resources/IResourceManager;Lnet/minecraft/client/renderer/color/BlockColors;Z)V",
				at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Maps;newHashMap()Ljava/util/HashMap;", remap = false),
				slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/model/ModelBakery;unbakedModelLoadingQueue:Ljava/util/Set;", opcode = Opcodes.PUTFIELD),
								to = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/model/ModelBakery;unbakedModels:Ljava/util/Map;", opcode = Opcodes.PUTFIELD)))
	private @Coerce Map<ResourceLocation, IUnbakedModel> betterUnbakedMap() {
		return new Object2ObjectOpenHashMap<>();
	}

	@NoCast
	@Redirect(method = "<init>(Lnet/minecraft/resources/IResourceManager;Lnet/minecraft/client/renderer/color/BlockColors;Z)V",
				at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Maps;newHashMap()Ljava/util/HashMap;", remap = false),
				slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/model/ModelBakery;unbakedModels:Ljava/util/Map;", opcode = Opcodes.PUTFIELD),
								to = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/model/ModelBakery;bakedModels:Ljava/util/Map;", opcode = Opcodes.PUTFIELD)))
	private @Coerce Map<?, IBakedModel> betterBakedMap() {
		return new Object2ReferenceOpenCustomHashMap<>(ModelKey.HASH_STRATEGY);
	}

	@NoCast
	@Redirect(method = "<init>(Lnet/minecraft/resources/IResourceManager;Lnet/minecraft/client/renderer/color/BlockColors;Z)V",
				at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Maps;newHashMap()Ljava/util/HashMap;", remap = false),
				slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/model/ModelBakery;topUnbakedModels:Ljava/util/Map;", opcode = Opcodes.PUTFIELD),
								to = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/model/ModelBakery;topBakedModels:Ljava/util/Map;", opcode = Opcodes.PUTFIELD)))
	private @Coerce Map<ResourceLocation, IBakedModel> betterTopBakedMap() {
		return new Object2ReferenceOpenHashMap<>();
	}

	@Inject(method = "uploadTextures", at = @At("RETURN"))
	private void clearFinishedMaps(CallbackInfoReturnable<SpriteMap> info) {
		topUnbakedModels = null;
		sheetData = null;
	}

	@NoCast(changing = 4, to = "pl/asie/foamfix/thready/ModelKey")
	@Redirect(method = "getBakedModel", remap = false, //Forge addition
			at = @At(value = "INVOKE", remap = false,
					target = "Lorg/apache/commons/lang3/tuple/Triple;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lorg/apache/commons/lang3/tuple/Triple;"))
	private @Coerce Object smallen(Object location, Object rotation, Object uvLock) {
		return new ModelKey((ResourceLocation) location, (TransformationMatrix) rotation, (Boolean) uvLock);
	}
}