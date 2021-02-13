package pl.asie.foamfix.blob;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.google.gson.InstanceCreator;
import com.google.gson.TypeAdapter;

class Special<T> {
	public final Class<T> name;
	private final InstanceCreator<T> maker;
	private final Predicate<Field> fieldFilter;
	private final Consumer<BiConsumer<Type, TypeAdapter<?>>> extraTypes;

	public Special(Class<T> name, InstanceCreator<T> maker) {
		this(name, maker, null, null);
	}

	public Special(Class<T> name, Predicate<Field> fieldFilter) {
		this(name, null, fieldFilter, null);
	}

	public Special(Class<T> name, Consumer<BiConsumer<Type, TypeAdapter<?>>> extraTypes) {
		this(name, null, null, extraTypes);
	}

	public Special(Class<T> name, InstanceCreator<T> maker, Consumer<BiConsumer<Type, TypeAdapter<?>>> extraTypes) {
		this(name, maker, null, extraTypes);
	}

	public Special(Class<T> name, Predicate<Field> fieldFilter, Consumer<BiConsumer<Type, TypeAdapter<?>>> extraTypes) {
		this(name, null, fieldFilter, extraTypes);
	}

	public Special(Class<T> name, InstanceCreator<T> maker, Predicate<Field> fieldFilter, Consumer<BiConsumer<Type, TypeAdapter<?>>> extraTypes) {
		this.name = name;
		this.maker = maker;
		this.fieldFilter = fieldFilter;
		this.extraTypes = extraTypes;
	}

	public void appendExtraTypes(BiConsumer<Type, TypeAdapter<?>> adapterConsumer) {
		if (extraTypes != null) extraTypes.accept(adapterConsumer);
	}

	public boolean hasMaker() {
		return maker != null;
	}

	public InstanceCreator<T> maker() {
		if (!hasMaker()) throw new IllegalStateException("Have no custom maker for " + name);
		return maker;
	}

	public T make(Type type) {
		return maker().createInstance(type);
	}

	public Predicate<Field> attach(Predicate<Field> normal) {
		return fieldFilter != null ? normal.and(fieldFilter) : normal;
	}
}