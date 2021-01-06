package pl.asie.foamfix.mixin.client;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.authlib.minecraft.SocialInteractionsService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;

import net.minecraft.client.GameConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;

@Mixin(Minecraft.class)
abstract class MinecraftMixin {
	@Redirect(method = "<init>",
				at = @At(value = "INVOKE",
						target = "Lnet/minecraft/client/Minecraft;func_244735_a(Lcom/mojang/authlib/yggdrasil/YggdrasilAuthenticationService;"
									+ "Lnet/minecraft/client/GameConfiguration;)Lcom/mojang/authlib/minecraft/SocialInteractionsService;"))
	private SocialInteractionsService loadElsewhere(Minecraft self, YggdrasilAuthenticationService service, GameConfiguration config) {
		return new SocialInteractionsService() {
			private final CompletableFuture<SocialInteractionsService> task = CompletableFuture.supplyAsync(() -> {
				return func_244735_a(service, config);
			}, Util.getServerExecutor());

			@Override
			public boolean serversAllowed() {
				return task.join().serversAllowed();
			}

			@Override
			public boolean realmsAllowed() {
				return task.join().realmsAllowed();
			}

			@Override
			public boolean isBlockedPlayer(UUID playerID) {
				return task.join().isBlockedPlayer(playerID);
			}

			@Override
			public boolean chatAllowed() {
				return task.join().chatAllowed();
			}
		};
	}

	@Shadow
	private SocialInteractionsService func_244735_a(YggdrasilAuthenticationService service, GameConfiguration config) {
		throw new AssertionError("Shadow didn't apply");
	}
}