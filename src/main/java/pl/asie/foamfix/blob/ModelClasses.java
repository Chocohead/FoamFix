package pl.asie.foamfix.blob;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericSignatureFormatError;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

import com.google.common.collect.Sets;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

public class ModelClasses {
	public static class ThingType {
		public final Class<?> type;
		private final Set<ThingType> subTypes = new ReferenceOpenHashSet<>();
		private final List<Object> instances = new ArrayList<>();

		public ThingType(Class<?> clazz) {
			type = clazz;
		}

		public void addInstance(Object instance) {
			if (Objects.requireNonNull(instance, "instance can't be null").getClass() != type) {
				throw new IllegalArgumentException("Expected " + type + " but instance was " + instance.getClass());
			}

			instances.add(instance);
		}

		public void addChild(ThingType child) {
			subTypes.add(child);
		}

		public Set<ThingType> getChildren() {
			return Collections.unmodifiableSet(subTypes);
		}

		public Set<ThingType> scanFields(Map<Class<?>, ThingType> existing) {
			Set<ThingType> extra = new ObjectOpenHashSet<>();

			for (Field field : type.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers())) continue; //Not bothered about static fields
				Type fieldType;
				try {
					fieldType = field.getGenericType();
				} catch (GenericSignatureFormatError | TypeNotPresentException | MalformedParameterizedTypeException e) {
					System.err.println("Error reading signature for field: " + field.getDeclaringClass() + '#' + field.getName());
					fieldType = field.getType();
				}

				for (Class<?> type : digType(fieldType)) {
					if (!existing.containsKey(type) && !type.isPrimitive() && !type.isInterface()) {
						extra.addAll(liftType(existing, type));
					}
				}
			}

			return extra;
		}

		private static Set<Class<?>> digType(Type type) {
			Set<Class<?>> extra = new ObjectOpenHashSet<>();

			while (type instanceof GenericArrayType) {
				//Unpack generic arrays out into a real type
				type = ((GenericArrayType) type).getGenericComponentType();
			}

			Class<?> concrete;
			if (type instanceof ParameterizedType) {
				Type rawType = ((ParameterizedType) type).getRawType();
				if (rawType.getClass() == Class.class) {
					concrete = (Class<?>) rawType;
				} else {
					System.err.println("Unexpected raw type: " + rawType + " (" + rawType.getClass() + ", " + Arrays.toString(rawType.getClass().getInterfaces()) + ')');
					return Collections.emptySet();
				}

				Type[] generics = ((ParameterizedType) type).getActualTypeArguments();
				if (generics.length > 0) {
					for (Type generic : generics) {
						extra.addAll(digType(generic));
					}
				}
			} else if (type.getClass() == Class.class) {
				concrete = (Class<?>) type;
			} else if (type instanceof WildcardType || type instanceof TypeVariable<?>) {
				return Collections.emptySet();
			} else {
				System.err.println("Unexpected type: " + type + " (" + type.getClass() + ", " + Arrays.toString(type.getClass().getInterfaces()) + ')');
				return Collections.emptySet();
			}

			//Unpack normal arrays into a real type
			while (concrete.isArray()) concrete = concrete.getComponentType();

			extra.add(concrete);
			return extra;
		}

		@Override
		public String toString() {
			return "Type[" + type + ']';
		}

		@Override
		public int hashCode() {
			return type.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return obj == this || (obj instanceof ThingType && type == ((ThingType) obj).type);
		}
	}

	public static void note(Path log, Collection<?> things) {
		note(log, things, false);
	}

	public static void note(Path log, Collection<?> things, boolean useInstances) {
		Map<Class<?>, ThingType> seenTypes = new IdentityHashMap<>();

		for (Object thing : things) {
			if (thing == null) continue; //Unimportant
			Class<?> clazz = thing.getClass();

			ThingType type = seenTypes.computeIfAbsent(clazz, ThingType::new);
			if (useInstances) type.addInstance(thing);

			liftType(seenTypes, type, clazz);
		}

		//Save the root types ready to pull additional types from fields/instances
		Map<Class<?>, ThingType> rootTypes = new IdentityHashMap<>(seenTypes);

		Queue<ThingType> toCheck = new ArrayDeque<>(seenTypes.values());
		while (!toCheck.isEmpty()) {
			ThingType type = toCheck.poll();

			toCheck.addAll(type.scanFields(seenTypes));
		}

		try (BufferedWriter out = Files.newBufferedWriter(log)) {
			out.write("Having read ");
			out.write(Integer.toString(things.size()));
			out.write(" objects, found ");
			out.write(Integer.toString(rootTypes.size()));
			out.write(" types (");
			out.write(Integer.toString(seenTypes.size()));
			out.write(" found in total)");
			out.newLine();
			out.newLine();

			ThingType object = rootTypes.remove(Object.class);
			printType(out, rootTypes, object, "");

			out.newLine();
			out.newLine();
			out.write(rootTypes.toString());
			out.newLine();

			out.newLine();
			out.newLine();

			for (ThingType type : seenTypes.values()) {
				if (type.type.getModifiers() > 0) {
					out.write(Modifier.toString(type.type.getModifiers()));
					out.write(' ');
				}
				out.write(type.type.getName());
				out.newLine();

				List<Field> fields = new ArrayList<>();
				for (Field field : type.type.getDeclaredFields()) {
					if (Modifier.isStatic(field.getModifiers())) {
						out.write('\t');
						out.write(Modifier.toString(field.getModifiers()));
						out.write(' ');
						try {
							out.write(field.getGenericType().getTypeName());
						} catch (GenericSignatureFormatError | TypeNotPresentException | MalformedParameterizedTypeException e) {
							out.write(field.getType().getTypeName());
							out.write(" (signature threw " + e.getClass().getSimpleName() + ')');
						}
						out.write(' ');
						out.write(field.getName());
						out.newLine();
					} else {
						fields.add(field);
					}
				}
				for (Field field : fields) {
					out.write('\t');
					if (field.getModifiers() > 0) {
						out.write(Modifier.toString(field.getModifiers()));
						out.write(' ');
					}
					try {
						out.write(field.getGenericType().getTypeName());
					} catch (GenericSignatureFormatError | TypeNotPresentException | MalformedParameterizedTypeException e) {
						out.write(field.getType().getTypeName());
						out.write(" (signature threw " + e.getClass().getSimpleName() + ')');
					}
					out.write(' ');
					out.write(field.getName());
					out.newLine();
				}

				out.newLine();
			}
		} catch (IOException e) {
			throw new RuntimeException("Error writing to " + log, e);
		}
	}

	public static void noteMain(Path log, Collection<?> things) {
		Map<Class<?>, ThingType> seenTypes = new IdentityHashMap<>();

		for (Object thing : things) {
			if (thing == null) continue; //Unimportant
			Class<?> clazz = thing.getClass();

			ThingType type = seenTypes.computeIfAbsent(clazz, ThingType::new);

			liftType(seenTypes, type, clazz);
		}

		try (BufferedWriter out = Files.newBufferedWriter(log)) {
			out.write("Having read ");
			out.write(Integer.toString(things.size()));
			out.write(" objects, found ");
			out.write(Integer.toString(seenTypes.size()));
			out.write(" types");
			out.newLine();
			out.newLine();

			ThingType object = seenTypes.remove(Object.class);
			printLongType(out, seenTypes, object, "");			
		} catch (IOException e) {
			throw new RuntimeException("Error writing to " + log, e);
		}
	}

	static Set<ThingType> liftType(Map<Class<?>, ThingType> seenTypes, Class<?> clazz) {
		ThingType type = seenTypes.get(clazz);
		if (type != null) return liftType(seenTypes, type, clazz);

		type = new ThingType(clazz);
		seenTypes.put(clazz, type);
		return Sets.union(Collections.singleton(type), liftType(seenTypes, type, clazz));
	}

	private static Set<ThingType> liftType(Map<Class<?>, ThingType> seenTypes, ThingType type, Class<?> clazz) {
		assert type.type == clazz;
		Set<ThingType> extra = new ObjectOpenHashSet<>();

		for (clazz = clazz.getSuperclass(); clazz != null && !seenTypes.containsKey(clazz); clazz = clazz.getSuperclass()) {
			ThingType parentType = new ThingType(clazz);

			extra.add(parentType);
			seenTypes.put(clazz, parentType);

			parentType.addChild(type);
			type = parentType;
		}

		if (clazz != null) {
			assert type.type.getSuperclass() == clazz;
			seenTypes.get(clazz).addChild(type);
		}

		return extra;
	}

	private static void printType(BufferedWriter out, Map<Class<?>, ThingType> types, ThingType root, String prefix) throws IOException {
		out.write(prefix);
		out.write(root.type.getName());
		out.newLine();

		ThingType[] children = new ThingType[root.getChildren().size()];
		int end = 0;
		for (ThingType type : root.getChildren()) {
			if (types.containsKey(type.type)) {
				children[end++] = type;
			}
		}
		Arrays.sort(children, 0, end, Comparator.comparing(type -> type.type.getName(), String.CASE_INSENSITIVE_ORDER));

		prefix = prefix.concat("\t");
		for (int i = 0; i < end; i++) {
			printType(out, types, children[i], prefix);
		}
	}

	private static void printLongType(BufferedWriter out, Map<Class<?>, ThingType> types, ThingType root, String prefix) throws IOException {
		out.write(prefix);
		if (root.type.getModifiers() > 0) {
			out.write(Modifier.toString(root.type.getModifiers()));
			out.write(' ');
		}
		out.write(root.type.getName());
		out.newLine();

		prefix = prefix.concat("\t");

		boolean wroteFields = false;
		List<Field> fields = new ArrayList<>();
		for (Field field : root.type.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
				out.write(prefix);
				out.write(Modifier.toString(field.getModifiers()));
				out.write(' ');
				try {
					out.write(field.getGenericType().getTypeName());
				} catch (GenericSignatureFormatError | TypeNotPresentException | MalformedParameterizedTypeException e) {
					out.write(field.getType().getTypeName());
					out.write(" (signature threw " + e.getClass().getSimpleName() + ')');
				}
				out.write(' ');
				out.write(field.getName());
				out.newLine();
				wroteFields = true;
			} else {
				fields.add(field);
			}
		}
		for (Field field : fields) {
			out.write(prefix);
			if (field.getModifiers() > 0) {
				out.write(Modifier.toString(field.getModifiers()));
				out.write(' ');
			}
			try {
				out.write(field.getGenericType().getTypeName());
			} catch (GenericSignatureFormatError | TypeNotPresentException | MalformedParameterizedTypeException e) {
				out.write(field.getType().getTypeName());
				out.write(" (signature threw " + e.getClass().getSimpleName() + ')');
			}
			out.write(' ');
			out.write(field.getName());
			out.newLine();
		}

		//Only add the extra space if there's anything to space out
		if (wroteFields || !fields.isEmpty()) out.newLine();

		for (ThingType child : root.getChildren().stream().sorted(Comparator.comparing(type -> type.type.getName(), String.CASE_INSENSITIVE_ORDER)).toArray(ThingType[]::new)) {
			printLongType(out, types, child, prefix);
		}
	}
 }