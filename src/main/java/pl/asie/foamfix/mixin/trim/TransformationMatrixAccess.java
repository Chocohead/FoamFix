package pl.asie.foamfix.mixin.trim;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.TransformationMatrix;

@Mixin(TransformationMatrix.class)
public interface TransformationMatrixAccess {
	@Accessor("matrix")
	Matrix4f getRawMatrix();
}