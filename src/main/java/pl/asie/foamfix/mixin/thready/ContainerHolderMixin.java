package pl.asie.foamfix.mixin.thready;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BlockModelDefinition.ContainerHolder;
import net.minecraft.state.StateContainer;

@Mixin(ContainerHolder.class)
abstract class ContainerHolderMixin {
	@Unique
	private final ThreadLocal<StateContainer<Block, BlockState>> containerHolder = ThreadLocal.withInitial(() -> {
		throw new UnsupportedOperationException("Called without context!");
	});

	@Overwrite
	public StateContainer<Block, BlockState> getStateContainer() {
		return containerHolder.get();
	}

	@Overwrite
	public void setStateContainer(StateContainer<Block, BlockState> stateContainer) {
		containerHolder.set(stateContainer);
	}
}