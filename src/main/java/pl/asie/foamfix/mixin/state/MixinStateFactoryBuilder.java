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

package pl.asie.foamfix.mixin.state;

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.state.IProperty;
import net.minecraft.state.IStateHolder;
import net.minecraft.state.StateContainer;
import net.minecraft.state.StateContainer.IFactory;
import net.minecraft.state.StateHolder;

import pl.asie.foamfix.state.FoamyStateFactory;

@Mixin(StateContainer.Builder.class)
public class MixinStateFactoryBuilder<O, S extends IStateHolder<S>> {
	@Shadow
	private @Final O owner;
	@Shadow
	private @Final Map<String, IProperty<?>> properties;

	@Inject(at = @At("HEAD"), method = "create", cancellable = true)
	public <A extends StateHolder<O, S>> void beforeBuild(IFactory<O, S, A> factory, CallbackInfoReturnable<StateContainer<O, S>> info) {
		if (FoamyStateFactory.hasFactory(owner)) {
			info.setReturnValue(new FoamyStateFactory<>(owner, factory, properties));
			info.cancel();
		}
	}
}
