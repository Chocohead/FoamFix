package pl.asie.foamfix.blob;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.function.BiConsumer;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import net.minecraft.client.renderer.model.IBakedModel;

abstract class Special<T extends IBakedModel> extends TypeAdapter<T> {
	public Special() {
	}

	protected void appendExtraTypes(BiConsumer<Type, TypeAdapter<?>> typeAdapter) {
	}

	protected static void withType(Gson gson, JsonWriter out, Object thing) throws IOException {
		out.beginObject();

		out.name("type");
		out.value(thing.getClass().getName());
		out.name("blob");
		gson.toJson(thing, thing.getClass(), out);

		out.endObject();
	}

	protected static <T> T withType(Gson gson, JsonReader in) throws IOException {
		in.beginObject();

		String name = in.nextName();
		String type;
		if ("type".equals(name)) {
			type = in.nextString();
		} else {
			throw new UnsupportedOperationException("TODO: Read out of order names: " + name);
		}

		Class<?> clazz;
		try {
			clazz = Class.forName(type);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Unable to reflectively create " + type, e);
		}

		name = in.nextName();
		if (!"blob".equals(name)) {
			throw new UnsupportedOperationException("Unexpected name: " + name);
		}

		T out = gson.fromJson(in, clazz);
		in.endObject();
		return out;
	}
}