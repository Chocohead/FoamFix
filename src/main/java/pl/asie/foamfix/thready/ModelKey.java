package pl.asie.foamfix.thready;

import java.util.Objects;

import org.apache.commons.lang3.tuple.Triple;

import it.unimi.dsi.fastutil.Hash.Strategy;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.TransformationMatrix;

import pl.asie.foamfix.mixin.trim.TransformationMatrixAccess;

public class ModelKey {
	public static final Strategy<ModelKey> HASH_STRATEGY = new Strategy<ModelKey>() {
		@Override
		public int hashCode(ModelKey key) {
			return key.hashCode();
		}

		@Override
		public boolean equals(ModelKey a, ModelKey b) {
			assert b == null || b instanceof ModelKey;
			return b == null ? a == null : b.equals(a);
		}
	};
	public final ResourceLocation location;
	public final Matrix4f rotation;
	public final boolean uvLock;

	public ModelKey(Triple<ResourceLocation, TransformationMatrix, Boolean> pack) {
		this(pack.getLeft(), pack.getMiddle(), pack.getRight());
	}

	public ModelKey(ResourceLocation location, TransformationMatrix rotation, Boolean uvLock) {
		this(location, rotation, uvLock.booleanValue());
	}

	public ModelKey(ResourceLocation location, TransformationMatrix rotation, boolean uvLock) {
		this(location, ((TransformationMatrixAccess) (Object) rotation).getRawMatrix(), uvLock);
	}

	public ModelKey(ResourceLocation location, Matrix4f rotation, boolean uvLock) {
		this.location = location;
		this.rotation = rotation;
		this.uvLock = uvLock;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(location) ^ (31 + Objects.hashCode(rotation)) ^ Boolean.hashCode(uvLock);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;

		if (obj instanceof ModelKey) {
			ModelKey other = (ModelKey) obj;
			return Objects.equals(location, other.location) && Objects.equals(rotation, other.rotation) && uvLock == other.uvLock;
		} else if (obj instanceof Triple) {
			Triple<?, ?, ?> other = (Triple<?, ?, ?>) obj;
			return other.getLeft() instanceof ResourceLocation && Objects.equals(location, other.getLeft()) &&
					other.getMiddle() instanceof TransformationMatrix && Objects.equals(rotation, ((TransformationMatrix) other.getMiddle()).getMatrix()) &&
					other.getRight() instanceof Boolean && uvLock == (Boolean) other.getRight();
		}

		return false;
	}
}