package pl.asie.foamfix.mixin;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;

import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import pl.asie.foamfix.FoamyConfig;

public class Plugin implements IMixinConfigPlugin {
	private static final String PACKAGE = "pl.asie.foamfix.mixin";

	@Override
	public void onLoad(String mixinPackage) {
		if (!PACKAGE.equals(mixinPackage)) throw new AssertionError("Unexpected Mixin package: " + mixinPackage);
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	public List<String> getMixins() {
		return Collections.emptyList();
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if (!mixinClassName.startsWith(PACKAGE)) {
			System.err.println("Unexpected Mixin prefix: " + mixinClassName + " targetting " + targetClassName);
			return false; //Doesn't look like one of ours
		}

		int split = mixinClassName.lastIndexOf('.');
		if (split <= 0) return true; //Generic Mixin

		switch (mixinClassName.substring(PACKAGE.length() + 1, split)) {
		case "trim":
			return FoamyConfig.TRIM_LISTS.asBoolean();

		case "state":
			return FoamyConfig.STATES.asBoolean();

		case "multipart.pool":
			return FoamyConfig.POOL_MULTIPART_PREDICATES.asBoolean();

		case "multipart.better":
			return FoamyConfig.BETTER_MULTIPART.asBoolean();

		case "multipart.best":
			return FoamyConfig.REPLACE_MULTIPART.asBoolean();

		case "memory":
			return FoamyConfig.RECYCLE_IDENTIFIERS.asBoolean();

		case "dfu":
			return FoamyConfig.CULL_DFU.asBoolean();

		default:
			return true;
		}
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}
}