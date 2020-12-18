package pl.asie.foamfix.state;

import net.minecraft.state.IProperty;
import net.minecraft.state.IStateHolder;

import pl.asie.foamfix.state.PropertyOrdering.Entry;

public class SinglePropertyValueMapper<C extends IStateHolder<C>> implements PropertyValueMapper<C> {
	private final Entry property;
	private final C[] states;

	@SuppressWarnings("unchecked")
	public SinglePropertyValueMapper(IProperty<?> property) {
		this.property = PropertyOrdering.getEntry(property);
		states = (C[]) new IStateHolder[property.getAllowedValues().size()];
	}

	@Override
	public int generateValue(C state) {
		int value = property.get(state.get(property.property));
		states[value] = state;
		return value;
	}

	@Override
	public <T extends Comparable<T>, V extends T> C with(int existingValue, IProperty<T> property, V propertyValue) {
		//We've only got one property so the given one should be the same
		if (this.property.property != property) return null;

		int value = this.property.get(propertyValue);
		if (value < 0) return null;

		return states[value];
	}
}