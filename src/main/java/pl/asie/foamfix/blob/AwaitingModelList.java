package pl.asie.foamfix.blob;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.client.renderer.model.IBakedModel;

class AwaitingModelList<T> extends AwaitingModel<T> {
	private final Function<T, List<IBakedModel>> getter;

	public AwaitingModelList(Function<T, List<IBakedModel>> getter) {
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
	protected void fix(Map<?, IBakedModel> models, T instance) {
		for (ListIterator<IBakedModel> it = getter.apply(instance).listIterator(); it.hasNext();) {
			IBakedModel model = it.next();

			if (model instanceof DelayedModel) {
				it.set(fetchModel(models, ((DelayedModel) model).real));
			}
		}
	}
}