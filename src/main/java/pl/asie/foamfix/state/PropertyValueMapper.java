package pl.asie.foamfix.state;

import net.minecraft.state.IProperty;
import net.minecraft.state.IStateHolder;

public interface PropertyValueMapper<C extends IStateHolder<C>> {
	<T extends Comparable<T>, V extends T> C with(int value, IProperty<T> property, V propertyValue);
	int generateValue(C state);
}