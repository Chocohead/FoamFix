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

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.screen.ConfirmBackupScreen;
import net.minecraft.client.gui.screen.ConfirmBackupScreen.ICallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

@Mixin(ConfirmBackupScreen.class)
abstract class BackupPromptScreenMixin extends Screen {
	@Shadow
	private @Final Screen parentScreen;
	@Shadow
	@Mutable
	private @Final ITextComponent message;
	@Shadow
	private IBidiRenderer field_243275_q;
	@Unique
	private boolean isBad;

	private BackupPromptScreenMixin() {
		super(null);
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void isBad(Screen parent, ICallback callback, ITextComponent title, ITextComponent subtitle, boolean showEraseCacheCheckBox, CallbackInfo info) {
		if (subtitle instanceof TranslationTextComponent && "selectWorld.backupWarning".equals(((TranslationTextComponent) subtitle).getKey())) {
			isBad = true;

			Object[] args = ((TranslationTextComponent) subtitle).getFormatArgs();
			String oldVersion = args[0] instanceof ITextComponent ? ((ITextComponent) args[0]).getString() : (String) args[0];
			String thisVersion = (String) args[1];
			this.message = new StringTextComponent("This world was last played in version " + oldVersion + "; you are now on version " + thisVersion + 
													". Please disable FoamFix's DFU removal in the config to update the world.");
		}
	}

	@Inject(method = "init", at = @At("HEAD"), cancellable = true)
	private void badInit(CallbackInfo info) {
		if (isBad) {
			field_243275_q = IBidiRenderer.func_243258_a(font, message, width - 50);

			int messageHeight = (field_243275_q.func_241862_a() + 1) * 9;
			addButton(new Button(width / 2 - 155 + 80, 124 + messageHeight, 150, 20, DialogTexts.GUI_CANCEL, (buttonWidget) -> {
				minecraft.displayGuiScreen(parentScreen);
			}));

			info.cancel();
		}
	}
}