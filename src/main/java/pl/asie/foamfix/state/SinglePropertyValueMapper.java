package pl.asie.foamfix.state;

import net.minecraft.state.Property;
import net.minecraft.state.StateHolder;

import pl.asie.foamfix.state.PropertyOrdering.Entry;

public class SinglePropertyValueMapper<C extends StateHolder<?, C>> implements PropertyValueMapper<C> {
	private final Entry property;
	private final C[] states;

	@SuppressWarnings("unchecked")
	public SinglePropertyValueMapper(Property<?> property) {
		this.property = PropertyOrdering.getEntry(property);
		states = (C[]) new StateHolder[property.getAllowedValues().size()];
	}

	@Override
	public int generateValue(C state) {
		int value = property.get(state.get(property.property));
		states[value] = state;
		return value;
	}

	@Override
	public <T extends Comparable<T>, V extends T> C with(int existingValue, Property<T> property, V propertyValue) {
		//We've only got one property so the given one should be the same
		if (!this.property.property.equals(property)) return null;

		int value = this.property.get(propertyValue);
		if (value < 0) return null;

		return states[value];
	}
}