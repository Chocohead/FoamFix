package pl.asie.foamfix.mixin;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.Object2ReferenceArrayMap;

import org.apache.commons.lang3.StringUtils;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.SignatureRemapper;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.mixin.transformer.ClassInfo.Method;
import org.spongepowered.asm.util.Annotations;
import org.spongepowered.asm.util.Locals;

import pl.asie.foamfix.FoamyConfig;

public class Plugin implements IMixinConfigPlugin {
	private static final String PACKAGE = "pl.asie.foamfix.mixin";

	@Override
	public void onLoad(String mixinPackage) {
		if (!PACKAGE.equals(mixinPackage)) throw new AssertionError("Unexpected Mixin package: " + mixinPackage);
		NoCast.class.getName(); //Load here so it can sit in the Mixin package
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
			return FoamyConfig.RECYCLE_IDENTIFIERS.asBoolean() && (!FoamyConfig.THREAD_MODELS.asBoolean() || !"ModelBakeryMixin".equals(mixinClassName.substring(split + 1)));

		case "thready":
			return FoamyConfig.THREAD_MODELS.asBoolean();

		case "dfu":
			return FoamyConfig.CULL_DFU.asBoolean();

		case "blob":
			return FoamyConfig.CACHE_MODELS.asBoolean();

		default:
			return true;
		}
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	private static Map<String, AnnotationNode> rename(Map<String, AnnotationNode> map, Iterable<Method> methods) {
		Map<String, AnnotationNode> out = new Object2ReferenceArrayMap<>(map.size());

		for (Method method : methods) {
			AnnotationNode node = map.get(method.getOriginalName().concat(method.getOriginalDesc()));
			if (node != null) out.put(method.getName().concat(method.getDesc()), node);
		}

		return out;
	}

	private static LabelNode findNextLabel(AbstractInsnNode insn) {
		while (insn.getType() != AbstractInsnNode.LABEL) {
			if ((insn = insn.getNext()) == null) return null;
		}

		return (LabelNode) insn;
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		Map<String, AnnotationNode> methods = new Object2ReferenceArrayMap<>();

		for (MethodNode method : mixinInfo.getClassNode(ClassReader.SKIP_CODE).methods) {
			AnnotationNode node = Annotations.getInvisible(method, NoCast.class);
			if (node != null) methods.put(method.name.concat(method.desc), node);
		}

		if (!methods.isEmpty()) {
			ClassInfo mixin = ClassInfo.fromCache(mixinClassName);
			if (mixin == null) throw new IllegalStateException("Cannot find Mixin at " + mixinClassName + " (targetting " + targetClassName + ')');

			methods = rename(methods, mixin.getMethods());
			targetClassName = targetClassName.replace('.', '/');

			for (MethodNode method : targetClass.methods) {
				for (Iterator<AbstractInsnNode> it = method.instructions.iterator(); it.hasNext();) {
					AbstractInsnNode insn = it.next();

					if (insn.getType() == AbstractInsnNode.METHOD_INSN) {
						MethodInsnNode call = (MethodInsnNode) insn;
						if (!targetClassName.equals(call.owner)) continue;

						AnnotationNode node = methods.get(call.name.concat(call.desc));
						if (node == null) continue;

						insn = it.next();
						if (insn.getType() != AbstractInsnNode.TYPE_INSN || insn.getOpcode() != Opcodes.CHECKCAST) {
							throw new IllegalStateException("Unexpected instruction (" + insn.getType() + ", " + insn.getOpcode() + ") after handler");
						}
						it.remove(); //Remove the cast

						int change = Annotations.getValue(node, "changing", -1);
						if (change >= 0) {
							String to = Annotations.getValue(node, "to");
							if (StringUtils.isBlank(to)) throw new IllegalArgumentException("Missing value to change local variable type");

							LabelNode label = Annotations.getValue(node, "nextLabel", Boolean.TRUE) ? findNextLabel(call) : null;
							LocalVariableNode local = Locals.getLocalVariableAt(targetClass, method, label != null ? label : call, change);
							if (local == null) {
								System.err.printf("Unable to find local %d at %s#%s%s%n", change, targetClassName, method.name, method.desc);
								continue; //Possibly not that important
							}

							String changeTo;
							switch (to.charAt(0)) {
							case 'V':
							case 'Z':
							case 'C':
							case 'B':
							case 'S':
							case 'I':
							case 'F':
							case 'J':
							case 'D':
							case '[':
							case 'L':
								changeTo = Type.getType(to).getDescriptor();
								break;

							default:
								changeTo = Type.getObjectType(to.replace('.', '/')).getDescriptor();
							}

							if (!changeTo.equals(local.desc)) {//Check the type of the local variable to see if it needs changing
								if (local.signature != null) {
									SignatureWriter writer = new SignatureWriter();
									new SignatureReader(local.signature).accept(new SignatureRemapper(writer, new Remapper() {
										private final String existing = local.desc;

										@Override
										public String mapType(String internalName) {
											return existing.equals(internalName) ? changeTo : super.mapType(internalName);
										}
									}));
									local.signature = writer.toString();
								}

								local.desc = changeTo;
							}
						}
					}
				}
			}
		}
	}
}