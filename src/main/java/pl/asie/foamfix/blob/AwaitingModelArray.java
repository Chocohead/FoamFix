package pl.asie.foamfix.blob;

import java.util.Map;
import java.util.function.Function;

import net.minecraft.client.renderer.model.IBakedModel;

class AwaitingModelArray<T> extends AwaitingModel<T> {
	private final Function<T, IBakedModel[]> getter;

	public AwaitingModelArray(Function<T, IBakedModel[]> getter) {
		this.getter = getter;
	}

	@Override
	protected boolean needsFix(T instance) {
		for (IBakedModel model : getter.apply(instance)) {
			if (model instanceof DelayedModel) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected void fix(Map<?, IBakedModel> allModels, T instance) {
		IBakedModel[] models = getter.apply(instance);

		for (int i = 0, end = models.length; i < end; i++) {
			if (models[i] instanceof DelayedModel) {
				models[i] = fetchModel(allModels, ((DelayedModel) models[i]).real);
			}
		}
	}
}