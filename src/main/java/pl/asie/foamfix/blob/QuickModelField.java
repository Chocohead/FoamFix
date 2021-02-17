package pl.asie.foamfix.blob;

import java.lang.reflect.Field;
import java.util.function.Function;

class QuickModelField<T> extends ModelField<T> {
	private final Function<T, Object> getter;

	public QuickModelField(Field field, Function<T, Object> getter) {
		super(field);

		this.getter = getter;
	}

	@Override
	protected Object get(T instance) throws ReflectiveOperationException {
		return getter.apply(instance);
	}
}