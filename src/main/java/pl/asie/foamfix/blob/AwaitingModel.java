package pl.asie.foamfix.blob;

import java.util.Map;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

import net.minecraft.client.renderer.model.IBakedModel;

abstract class AwaitingModel<T> {
	protected final Set<T> instances = new ReferenceOpenHashSet<>();

	public AwaitingModel() {
	}

	public void queue(T instance) {
		if (needsFix(instance)) {
			instances.add(instance);
		}
	}

	protected abstract boolean needsFix(T instance);

	public void fill(Map<?, IBakedModel> models) {
		for (T instance : instances) {
			fix(models, instance);
		}
	}

	protected static IBakedModel fetchModel(Map<?, IBakedModel> models, Object location) {
		IBakedModel model = models.get(location);
		if (model == null) {
			System.err.println("Unable to find requested model: " + location);
			return null; //Oh dear
		}

		return model;
	}

	protected abstract void fix(Map<?, IBakedModel> models, T instance);

	public void clear() {
		instances.clear();
	}
}