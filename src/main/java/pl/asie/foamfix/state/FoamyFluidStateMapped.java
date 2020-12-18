package pl.asie.foamfix.state;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.state.IProperty;

public class FoamyFluidStateMapped extends FluidState {
	private final PropertyValueMapper<IFluidState> owner;
	private int value;

	public FoamyFluidStateMapped(PropertyValueMapper<IFluidState> owner, Fluid fluid, ImmutableMap<IProperty<?>, Comparable<?>> properties) {
		super(fluid, properties);

		this.owner = owner;
	}

	@Override
	public <T extends Comparable<T>, V extends T> IFluidState with(IProperty<T> property, V value) {
		IFluidState state = owner.with(this.value, property, value);
		if (state != null) return state;

		Comparable<?> comparable = getValues().get(property);
		if (comparable == null) {
			throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + getFluid());
		} else {
			throw new IllegalArgumentException("Cannot set property " + property + " to " + value + " on block " + getFluid() + ", it is not an allowed value");
		}
	}

	@Override
	public void buildPropertyValueTable(Map<Map<IProperty<?>, Comparable<?>>, IFluidState> properties) {
		this.value = owner.generateValue(this);
	}
}