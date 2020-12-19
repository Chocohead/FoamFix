package pl.asie.foamfix.state;

import net.minecraft.state.Property;
import net.minecraft.state.StateHolder;

public interface PropertyValueMapper<C extends StateHolder<?, C>> {
	<T extends Comparable<T>, V extends T> C with(int value, Property<T> property, V propertyValue);
	int generateValue(C state);
}