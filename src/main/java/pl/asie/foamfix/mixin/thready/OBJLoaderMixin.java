package pl.asie.foamfix.mixin.thready;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.util.Util;

import net.minecraftforge.client.model.obj.OBJLoader;

import pl.asie.foamfix.FoamyCacherCleanser;
import pl.asie.foamfix.mixin.NoCast;

@Mixin(value = OBJLoader.class, remap = false)
abstract class OBJLoaderMixin {
	@NoCast
	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Maps;newHashMap()Ljava/util/HashMap;", remap = false), expect = 2)
	private @Coerce Map<?, ?> safe() {
		return Util.make(new ConcurrentHashMap<>(), map -> {
			FoamyCacherCleanser.addCleaner(map::clear);
		});
	}
}