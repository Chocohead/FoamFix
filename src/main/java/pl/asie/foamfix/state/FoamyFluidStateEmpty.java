package pl.asie.foamfix.state;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.state.IProperty;

public class FoamyFluidStateEmpty extends FluidState {
	public FoamyFluidStateEmpty(Fluid fluid) {
		super(fluid, ImmutableMap.of());
	}

	@Override
	public <T extends Comparable<T>, V extends T> IFluidState with(IProperty<T> property, V value) {
		throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + getFluid());
	}

	@Override
	public void buildPropertyValueTable(Map<Map<IProperty<?>, Comparable<?>>, IFluidState> properties) {
	}
}
