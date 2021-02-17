package pl.asie.foamfix.blob;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import net.minecraft.client.renderer.model.IBakedModel;

import net.minecraftforge.fml.unsafe.UnsafeHacks;

class ModelField<T> extends AwaitingModel<T> {
	private final Field field;

	public ModelField(Field field) {
		this.field = Objects.requireNonNull(field, "Null field received");
	}

	@Override
	protected boolean needsFix(T instance) {
		try {
			return get(instance) instanceof DelayedModel;
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Failed to read " + field + " from " + instance, e);
		}
	}

	protected Object get(T instance) throws ReflectiveOperationException {
		return field.get(instance);
	}

	@Override
	protected void fix(Map<?, IBakedModel> models, T instance) {
		try {
			DelayedModel model = (DelayedModel) get(instance);
			IBakedModel real = fetchModel(models, model.real);

			if (Modifier.isFinal(field.getModifiers())) {
				UnsafeHacks.setField(field, instance, real);
			} else {
				field.set(instance, real);
			}
		} catch (ReflectiveOperationException | ClassCastException e) {
			Object key = "<unknown>";
			for (Entry<?, IBakedModel> innerEntry : models.entrySet()) {
				if (innerEntry.getValue() == instance) {
					key = innerEntry.getKey();
					break;
				}
			}
			throw new RuntimeException("Failed to fill " + field + " in " + instance + " (" + key + ')', e);
		}
	}
}