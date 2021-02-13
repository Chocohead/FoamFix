package pl.asie.foamfix.blob;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Triple;

import com.google.common.collect.Streams;
import com.google.common.primitives.Primitives;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import it.unimi.dsi.fastutil.bytes.Byte2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.bytes.Byte2ByteOpenHashMap;
import it.unimi.dsi.fastutil.bytes.Byte2CharOpenHashMap;
import it.unimi.dsi.fastutil.bytes.Byte2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.bytes.Byte2FloatOpenHashMap;
import it.unimi.dsi.fastutil.bytes.Byte2IntOpenHashMap;
import it.unimi.dsi.fastutil.bytes.Byte2LongOpenHashMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.bytes.Byte2ShortOpenHashMap;
import it.unimi.dsi.fastutil.chars.Char2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.chars.Char2ByteOpenHashMap;
import it.unimi.dsi.fastutil.chars.Char2CharOpenHashMap;
import it.unimi.dsi.fastutil.chars.Char2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.chars.Char2FloatOpenHashMap;
import it.unimi.dsi.fastutil.chars.Char2IntOpenHashMap;
import it.unimi.dsi.fastutil.chars.Char2LongOpenHashMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.chars.Char2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.chars.Char2ShortOpenHashMap;
import it.unimi.dsi.fastutil.doubles.Double2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.doubles.Double2ByteOpenHashMap;
import it.unimi.dsi.fastutil.doubles.Double2CharOpenHashMap;
import it.unimi.dsi.fastutil.doubles.Double2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.doubles.Double2FloatOpenHashMap;
import it.unimi.dsi.fastutil.doubles.Double2IntOpenHashMap;
import it.unimi.dsi.fastutil.doubles.Double2LongOpenHashMap;
import it.unimi.dsi.fastutil.doubles.Double2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.doubles.Double2ShortOpenHashMap;
import it.unimi.dsi.fastutil.floats.Float2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.floats.Float2ByteOpenHashMap;
import it.unimi.dsi.fastutil.floats.Float2CharOpenHashMap;
import it.unimi.dsi.fastutil.floats.Float2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.floats.Float2FloatOpenHashMap;
import it.unimi.dsi.fastutil.floats.Float2IntOpenHashMap;
import it.unimi.dsi.fastutil.floats.Float2LongOpenHashMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.floats.Float2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.floats.Float2ShortOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ByteOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2CharOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2CharOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ShortOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2CharOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ByteOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2CharOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ShortOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ByteOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2CharOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2FloatOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2IntOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2LongOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ShortOpenHashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.BuiltInModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ItemTransformVec3f;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.common.IExtensibleEnum;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import team.chisel.ctm.api.model.IModelCTM;
import team.chisel.ctm.client.model.AbstractCTMBakedModel;
import team.chisel.ctm.client.model.ModelBakedCTM;
import team.chisel.ctm.client.model.ModelCTM;

import pl.asie.foamfix.FoamyCacherCleanser;
import pl.asie.foamfix.FoamyConfig;
import pl.asie.foamfix.mixin.blob.ItemOverrideListAccess;
import pl.asie.foamfix.mixin.blob.ModelBakeryAccess;
import pl.asie.foamfix.mixin.blob.ModelCTMAccess;
import pl.asie.foamfix.thready.ModelKey;

@SuppressWarnings("deprecation")
public class ModelSerialiser {
	private static class DelayedModel extends BuiltInModel {
		public final Object real;

		DelayedModel(Object real) {
			super(null, null, null, false);

			this.real = real;
		}
	}
	/*private static final Type ROOT_MAP = new Object() {
	}.getClass();*/
	@SuppressWarnings("serial") //Bit redundant for our purposes
	private static final Type MAP_TYPE = (FoamyConfig.THREAD_MODELS.asBoolean() ? new TypeToken<Map<ModelKey, IBakedModel>>() {
	} : new TypeToken<Map<Triple<ResourceLocation, TransformationMatrix, Boolean>, IBakedModel>>() {
	}).getType();
	private static Map<IBakedModel, Object> reverseModelMap = new IdentityHashMap<>();
	private static Map<Field, Set<IBakedModel>> fieldsToFix = new Object2ObjectOpenHashMap<>();
	private static final Map<Class<?>, Special<?>> SPECIAL_CASES = specialCases();
	private static final Gson GSON = builder()/*.registerTypeAdapter(ROOT_MAP, new TypeAdapter<Map<?, IBakedModel>>() {
		@Override
		public void write(JsonWriter out, Map<?, IBakedModel> value) throws IOException {
			out.beginObject();

			//Initial map size
			out.name("size");
			out.value(value.size());
			//Actual map contents
			out.name("models");
			out.beginArray();
			for (Entry<?, IBakedModel> entry : value.entrySet()) {
				out.beginArray();
				GSON.toJson(entry.getKey(), entry.getKey().getClass(), out);
				out.beginObject();
				out.name("type");
				out.value(entry.getValue().getClass().getName());
				
				out.endObject();
				out.endArray();
			}
			out.endArray();

			out.endObject();
		}

		@Override
		public Map<?, IBakedModel> read(JsonReader in) throws IOException {
			// TODO Auto-generated method stub
			return null;
		}
	}).registerTypeAdapter(SimpleBakedModel.class, new TypeAdapter<SimpleBakedModel>() {
		@Override
		public void write(JsonWriter out, SimpleBakedModel value) throws IOException {
			out.beginObject();

			

			out.endObject();
		}

		@Override
		public SimpleBakedModel read(JsonReader in) throws IOException {
			in.beginObject();

			

			in.endObject();
			return null;
		}
	}.nullSafe())*/.registerTypeAdapter(ModelKey.class, new TypeAdapter<ModelKey>() {
		private final Matrix4f identity = TransformationMatrix.identity().getMatrix();

		@Override
		public void write(JsonWriter out, ModelKey value) throws IOException {
			boolean identityRotation = value.rotation.equals(identity);
			boolean unlockedUV = !value.uvLock;

			if (identityRotation && unlockedUV) {
				GSON.toJson(value.location, ResourceLocation.class, out);
			} else {
				out.beginObject();

				out.name("location");
				GSON.toJson(value.location, ResourceLocation.class, out);
				if (!identityRotation) {
					out.name("rotation");
					GSON.toJson(value.rotation, Matrix4f.class, out);
				}
				if (!unlockedUV) {
					out.name("uvLock");
					out.value(value.uvLock);
				}

				out.endObject();
			}
		}

		@Override
		public ModelKey read(JsonReader in) throws IOException {
			ResourceLocation location = null;
			Matrix4f rotation = identity;
			boolean uvLock = false;

			switch (in.peek()) {
			case STRING:
				location = GSON.fromJson(in, ResourceLocation.class);
				break;

			case BEGIN_OBJECT: {
				in.beginObject();

				while (in.peek() != JsonToken.END_OBJECT) {
					String name = in.nextName();

					switch (name) {
					case "location":
						location = GSON.fromJson(in, ResourceLocation.class);
						break;

					case "rotation":
						rotation = GSON.fromJson(in, Matrix4f.class);
						break;

					case "uvLock":
						uvLock = in.nextBoolean();
						break;
					}
				}

				in.endObject();
				break;
			}

			default:
				throw new IllegalArgumentException("Unexpected JSON element: " + in.peek());
			}

			return new ModelKey(location, rotation, uvLock);
		}
	}.nullSafe())/*.registerTypeAdapter(Triple.class, new TypeAdapter<Triple<?, ?, ?>>() {
		@Override
		public void write(JsonWriter out, Triple<?, ?, ?> value) throws IOException {
			out.beginObject();

			if (value.getLeft() != null) {
				out.name("left");
				GSON.toJson(value.getLeft(), value.getLeft().getClass(), out);
			}
			if (value.getMiddle() != null) {
				out.name("middle");
				GSON.toJson(value.getMiddle(), value.getMiddle().getClass(), out);
			}
			if (value != null) {
				out.name("right");
				GSON.toJson(value.getRight(), value.getRight().getClass(), out);
			}

			out.endObject();
		}

		@Override
		public Triple<?, ?, ?> read(JsonReader in) throws IOException {
			in.beginObject();

			Object left = null, middle = null, right = null;

			while (in.peek() != JsonToken.END_OBJECT) {
				String JSONname = in.nextName();

				switch (JSONname) {
				case "left":
					left = GSON.fromJson(in, Object.class);
					break;

				case "middle":
					middle = GSON.fromJson(in, Object.class);
					break;

				case "right":
					right = GSON.fromJson(in, Object.class);
					break;
				}
			}

			in.endObject();
			return Triple.of(left, middle, right);
		}
	}.nullSafe())*/.registerTypeAdapterFactory(new TypeAdapterFactory() {
		private final Map<String, String> pool = Util.make(new Object2ObjectOpenHashMap<>(), map -> {
			FoamyCacherCleanser.addCleaner(() -> {//Once the instances are all made there is no need to remember them
				map.clear();
				map.trim();
			});
		});
		private final TypeAdapter<? extends ResourceLocation> adapter = new TypeAdapter<ResourceLocation>() {
			private final Map<String, ResourceLocation> innerPool = Util.make(new Object2ObjectOpenHashMap<>(), map -> {
				FoamyCacherCleanser.addCleaner(() -> {
					map.clear();
					map.trim();
				});
			});

			@Override
			public void write(JsonWriter out, ResourceLocation value) throws IOException {
				out.value(value.toString());
			}

			@Override
			public ResourceLocation read(JsonReader in) throws IOException {
				return innerPool.computeIfAbsent(in.nextString(), value -> {
					if (value.indexOf('#') >= 3) {//Simplest ModelResourceLocation is a:b#c
						ModelResourceLocation out = new ModelResourceLocation(value);

						pool.putIfAbsent(out.getNamespace(), out.getNamespace());
						pool.putIfAbsent(out.getPath(), out.getPath());
						pool.putIfAbsent(out.getVariant(), out.getVariant());

						return out;
					} else {
						ResourceLocation out = new ResourceLocation(value);

						return new ResourceLocation(pool.computeIfAbsent(out.getNamespace(), Function.identity()),
													pool.computeIfAbsent(out.getPath(), Function.identity()));
					}
				});
			}
		}.nullSafe();

		@Override
		@SuppressWarnings("unchecked")
		public <T> TypeAdapter<T> create(Gson gson, com.google.gson.reflect.TypeToken<T> type) {
			Class<? super T> rawType = type.getRawType();

			if (rawType == String.class) {
				return (TypeAdapter<T>) new TypeAdapter<String>() {
					@Override
					public void write(JsonWriter out, String value) throws IOException {
						out.value(value);
					}

					@Override
					public String read(JsonReader in) throws IOException {
						switch (in.peek()) {
						case NULL:
							in.nextNull();
							return null;

						case BOOLEAN: //Apparently this is a thing which happens
							return pool.computeIfAbsent(Boolean.toString(in.nextBoolean()), Function.identity()); 

						default:
							return pool.computeIfAbsent(in.nextString(), Function.identity());
						}
					}
				};
			} else if (rawType == ResourceLocation.class || rawType == ModelResourceLocation.class) {
				return (TypeAdapter<T>) adapter;
			} else {
				return null;
			}
		}
	}).registerTypeHierarchyAdapter(TextureAtlasSprite.class, new TypeAdapter<TextureAtlasSprite>() {
		@Override
		public void write(JsonWriter out, TextureAtlasSprite value) throws IOException {
			out.beginObject();

			out.name("atlas");
			GSON.toJson(value.getAtlasTexture().getTextureLocation(), ResourceLocation.class, out);
			out.name("name");
			GSON.toJson(value.getName(), ResourceLocation.class, out);

			out.endObject();
		}

		@Override
		public TextureAtlasSprite read(JsonReader in) throws IOException {
			in.beginObject();

			ResourceLocation atlas = null, name = null;

			while (in.peek() != JsonToken.END_OBJECT) {
				String JSONname = in.nextName();

				switch (JSONname) {
				case "atlas":
					atlas = GSON.fromJson(in, ResourceLocation.class);
					break;

				case "name":
					name = GSON.fromJson(in, ResourceLocation.class);
					break;
				}
			}

			in.endObject();
			return Minecraft.getInstance().getAtlasSpriteGetter(atlas).apply(name);
		}
	}.nullSafe()).registerTypeAdapter(Vector3f.class, new TypeAdapter<Vector3f>() {
		@Override
		public void write(JsonWriter out, Vector3f value) throws IOException {
			out.beginArray();

			out.value(value.getX());
			out.value(value.getY());
			out.value(value.getZ());

			out.endArray();
		}

		@Override
		public Vector3f read(JsonReader in) throws IOException {
			in.beginArray();

			float x = (float) in.nextDouble();
			float y = (float) in.nextDouble();
			float z = (float) in.nextDouble();

			in.endArray();
			return new Vector3f(x, y, z);
		}
	}.nullSafe()).registerTypeAdapter(ItemTransformVec3f.class, new TypeAdapter<ItemTransformVec3f>() {
		@Override
		public void write(JsonWriter out, ItemTransformVec3f value) throws IOException {
			if (ItemTransformVec3f.DEFAULT == value) {
				out.value("<default>");
			} else {
				out.beginObject();

				out.name("rotation");
				GSON.toJson(value.rotation, Vector3f.class, out);
				out.name("translation");
				GSON.toJson(value.translation, Vector3f.class, out);
				out.name("scale");
				GSON.toJson(value.scale, Vector3f.class, out);

				out.endObject();				
			}
		}

		@Override
		public ItemTransformVec3f read(JsonReader in) throws IOException {
			switch (in.peek()) {
			case STRING:
				in.nextString();
				return ItemTransformVec3f.DEFAULT;

			case BEGIN_OBJECT: {
				in.beginObject();

				Vector3f rotation = ItemTransformVec3f.DEFAULT.rotation;
				Vector3f translation = ItemTransformVec3f.DEFAULT.translation;
				Vector3f scale = ItemTransformVec3f.DEFAULT.scale;

				while (in.peek() != JsonToken.END_OBJECT) {
					String name = in.nextName();

					switch (name) {
					case "rotation":
						rotation = GSON.fromJson(in, Vector3f.class);
						break;

					case "translation":
						translation = GSON.fromJson(in, Vector3f.class);
						break;

					case "scale":
						scale = GSON.fromJson(in, Vector3f.class);
						break;

					default:
						break;
					}
				}

				in.endObject();
				return new ItemTransformVec3f(rotation, translation, scale);
			}

			default:
				throw new IllegalArgumentException("Unexpected JSON element: " + in.peek());
			}
		}
	}.nullSafe()).registerTypeAdapter(ItemCameraTransforms.class, new TypeAdapter<ItemCameraTransforms>() {
		@Override
		public void write(JsonWriter out, ItemCameraTransforms value) throws IOException {
			Set<TransformType> customTransforms = EnumSet.noneOf(TransformType.class);

			if (ItemCameraTransforms.DEFAULT != value) {
				for (TransformType transform : TransformType.values()) {
					if (value.hasCustomTransform(transform)) {
						customTransforms.add(transform);
					}
				}
			}

			if (customTransforms.isEmpty()) {
				out.value("<default>");
			} else {
				out.beginObject();

				for (TransformType transform : customTransforms) {
					out.name(transform.name());
					GSON.toJson(value.getTransform(transform), ItemTransformVec3f.class, out);
				}

				out.endObject();				
			}
		}

		@Override
		public ItemCameraTransforms read(JsonReader in) throws IOException {
			switch (in.peek()) {
			case STRING:
				in.nextString();
				return ItemCameraTransforms.DEFAULT;

			case BEGIN_OBJECT: {
				in.beginObject();

				ItemTransformVec3f thirdPersonLeft = ItemTransformVec3f.DEFAULT;
				ItemTransformVec3f thirdPersonRight = ItemTransformVec3f.DEFAULT;
				ItemTransformVec3f firstPersonLeft = ItemTransformVec3f.DEFAULT;
				ItemTransformVec3f firstPersonRight = ItemTransformVec3f.DEFAULT;
				ItemTransformVec3f head = ItemTransformVec3f.DEFAULT;
				ItemTransformVec3f gui = ItemTransformVec3f.DEFAULT;
				ItemTransformVec3f ground = ItemTransformVec3f.DEFAULT;
				ItemTransformVec3f fixed = ItemTransformVec3f.DEFAULT;

				while (in.peek() != JsonToken.END_OBJECT) {
					String name = in.nextName();

					switch (TransformType.valueOf(name)) {
					case THIRD_PERSON_LEFT_HAND:
						thirdPersonLeft = GSON.fromJson(in, ItemTransformVec3f.class);
						break;

					case THIRD_PERSON_RIGHT_HAND:
						thirdPersonRight = GSON.fromJson(in, ItemTransformVec3f.class);
						break;

					case FIRST_PERSON_LEFT_HAND:
						firstPersonLeft = GSON.fromJson(in, ItemTransformVec3f.class);
						break;

					case FIRST_PERSON_RIGHT_HAND:
						firstPersonRight = GSON.fromJson(in, ItemTransformVec3f.class);
						break;

					case HEAD:
						head = GSON.fromJson(in, ItemTransformVec3f.class);
						break;

					case GUI:
						gui = GSON.fromJson(in, ItemTransformVec3f.class);
						break;

					case GROUND:
						ground = GSON.fromJson(in, ItemTransformVec3f.class);
						break;

					case FIXED:
						fixed = GSON.fromJson(in, ItemTransformVec3f.class);
						break;

					case NONE:
					default:
						break;
					}
				}

				in.endObject();
				return new ItemCameraTransforms(thirdPersonLeft, thirdPersonRight, firstPersonLeft, firstPersonRight, head, gui, ground, fixed);
			}

			default:
				throw new IllegalArgumentException("Unexpected JSON element: " + in.peek());
			}
		}
	}.nullSafe()).registerTypeAdapterFactory(new TypeAdapterFactory() {
		private TypeAdapterFactory getOuter() {
			return this; //Can't reference this directly as an anonymous class
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> TypeAdapter<T> create(Gson gson, com.google.gson.reflect.TypeToken<T> type) {
			return type.getRawType() == ItemOverrideList.class ? (TypeAdapter<T>) new TypeAdapter<ItemOverrideList>() {
				private final TypeAdapter<ItemOverrideList> adapter = (TypeAdapter<ItemOverrideList>) gson.getDelegateAdapter(getOuter(), type);

				@Override
				public void write(JsonWriter out, ItemOverrideList value) throws IOException {
					if (ItemOverrideList.EMPTY == value) {
						out.value("<default>");
					} else {
						/*out.value(value.getOverrides().stream().map(ItemOverride::getLocation).collect(Collectors.mapping(ResourceLocation::toString,
																										Collectors.joining(", ", "TODO: ItemOverrideList: [", "]"))));*/
						adapter.write(out, value);
					}
				}

				@Override
				public ItemOverrideList read(JsonReader in) throws IOException {
					switch (in.peek()) {
					case STRING:
						in.nextString();
						return ItemOverrideList.EMPTY;

					case BEGIN_OBJECT: {
						return adapter.read(in);
					}

					default:
						throw new IllegalArgumentException("Unexpected JSON element: " + in.peek());
					}
				}
			}.nullSafe() : null;
		}
	})/*.registerTypeAdapter(SimpleBakedModel.class, new TypeAdapter<SimpleBakedModel>() {
		@SuppressWarnings("serial") //Bit silly
		private final Type quadList = new TypeToken<List<BakedQuad>>() {
		}.getType();
		@SuppressWarnings("serial")
		private final Type quadMap = new TypeToken<Map<Direction, List<BakedQuad>>>() {
		}.getType();

		@Override
		public void write(JsonWriter out, SimpleBakedModel value) throws IOException {
			out.beginObject();

			out.name("quads");
			GSON.toJson(value.getQuads(null, null, null), quadList, out);
			out.name("faceQuads");
			GSON.toJson(Arrays.stream(Direction.values()).collect(Collectors.toMap(Function.identity(), side -> value.getQuads(null, side, null))), quadMap, out);
			out.name("usesAO");
			out.value(value.isAmbientOcclusion());
			out.name("hasDepth");
			out.value(value.isGui3d());
			out.name("isSideLit");
			out.value(value.isSideLit());
			out.name("sprite");
			GSON.toJson(value.getParticleTexture(), TextureAtlasSprite.class, out);
			out.name("transformation");
			GSON.toJson(value.getItemCameraTransforms(), ItemCameraTransforms.class, out);
			out.name("itemOverrides");
			GSON.toJson(value.getOverrides(), ItemOverrideList.class, out);

			out.endObject();
		}

		@Override
		public SimpleBakedModel read(JsonReader in) throws IOException {
			in.beginObject();

			List<BakedQuad> quads = null;
			Map<Direction, List<BakedQuad>> faceQuads = null;
			boolean usesAO = false, hasDepth = false, isSideLit = false;
			TextureAtlasSprite sprite = null;
			ItemCameraTransforms transformation = null;
			ItemOverrideList itemOverrides = null;

			while (in.peek() != JsonToken.END_OBJECT) {
				String name = in.nextName();

				switch (name) {
				case "quads":
					quads = GSON.fromJson(in, quadList);
					break;

				case "faceQuads":
					faceQuads = GSON.fromJson(in, quadMap);
					break;

				case "usesAO":
					usesAO = in.nextBoolean();
					break;

				case "hasDepth":
					hasDepth = in.nextBoolean();
					break;

				case "isSideLit":
					isSideLit = in.nextBoolean();
					break;

				case "sprite":
					sprite = GSON.fromJson(in, TextureAtlasSprite.class);
					break;

				case "transformation":
					transformation = GSON.fromJson(in, ItemCameraTransforms.class);
					break;

				case "itemOverrides":
					itemOverrides = GSON.fromJson(in, ItemOverrideList.class);
					break;
				}
			}

			in.endObject();
			return new SimpleBakedModel(quads, new EnumMap<>(faceQuads), usesAO, isSideLit, hasDepth, sprite, transformation, itemOverrides);
		}
	}.nullSafe()).registerTypeAdapter(BuiltInModel.class, new TypeAdapter<BuiltInModel>() {
		@Override
		public void write(JsonWriter out, BuiltInModel value) throws IOException {
			out.beginObject();

			out.name("isSideLit");
			out.value(value.isSideLit());
			out.name("sprite");
			GSON.toJson(value.getParticleTexture(), TextureAtlasSprite.class, out);
			out.name("transformation");
			GSON.toJson(value.getItemCameraTransforms(), ItemCameraTransforms.class, out);
			out.name("itemOverrides");
			GSON.toJson(value.getOverrides(), ItemOverrideList.class, out);

			out.endObject();
		}

		@Override
		public BuiltInModel read(JsonReader in) throws IOException {
			in.beginObject();

			boolean isSideLit = false;
			TextureAtlasSprite sprite = null;
			ItemCameraTransforms transformation = null;
			ItemOverrideList itemOverrides = null;

			while (in.peek() != JsonToken.END_OBJECT) {
				String name = in.nextName();

				switch (name) {
				case "isSideLit":
					isSideLit = in.nextBoolean();
					break;

				case "sprite":
					sprite = GSON.fromJson(in, TextureAtlasSprite.class);
					break;

				case "transformation":
					transformation = GSON.fromJson(in, ItemCameraTransforms.class);
					break;

				case "itemOverrides":
					itemOverrides = GSON.fromJson(in, ItemOverrideList.class);
					break;
				}
			}

			in.endObject();
			return new BuiltInModel(transformation, itemOverrides, sprite, isSideLit);
		}
	}.nullSafe())*/.registerTypeHierarchyAdapter(IUnbakedModel.class, new TypeAdapter<IUnbakedModel>() {
		@Override
		public void write(JsonWriter out, IUnbakedModel value) throws IOException {
			for (Entry<ResourceLocation, IUnbakedModel> entry : ((ModelBakeryAccess) (Object) ModelLoader.instance()).getUnbakedModels().entrySet()) {
				if (entry.getValue() == value) {
					GSON.toJson(entry.getKey(), ResourceLocation.class, out);
					return;
				}
			}

			throw new IllegalArgumentException("Unregistered unbaked model: " + value);
		}

		@Override
		public IUnbakedModel read(JsonReader in) throws IOException {
			ResourceLocation model = GSON.fromJson(in, ResourceLocation.class);
			return ModelLoader.instance().getUnbakedModel(model);
		}
	}.nullSafe()).registerTypeHierarchyAdapter(IBakedModel.class, new TypeAdapter<IBakedModel>() {
		@SuppressWarnings("serial")
		private final Type mapKeyType = FoamyConfig.THREAD_MODELS.asBoolean() ? ModelKey.class : new TypeToken<Triple<ResourceLocation, TransformationMatrix, Boolean>>() {
		}.getType();
		private final Map<Class<? extends IBakedModel>, Field[]> fields = Util.make(new IdentityHashMap<>(), map -> {
			map.put(IBakedModel.class, new Field[0]);
		});
		private boolean active;
		private IBakedModel activeModel;
		private Field activeField;

		private Predicate<Field> special(Class<?> type, Predicate<Field> normal) {
			Special<?> specialCase = SPECIAL_CASES.get(type);
			return specialCase != null ? specialCase.attach(normal) : normal;
		}

		private Field[] findFields(Class<? extends IBakedModel> type) {
			Field[] out = fields.get(type);
			if (out != null) return out;

			Stream<Field> fields = Arrays.stream(type.getDeclaredFields()).filter(special(type, field -> {
				int modifiers = field.getModifiers();
				return !Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers);
			})).peek(field -> field.setAccessible(true));

			Class<?> parentType = type.getSuperclass();
			if (IBakedModel.class.isAssignableFrom(parentType)) {
				Field[] parentFields = findFields(parentType.asSubclass(IBakedModel.class));

				if (parentFields.length > 0) {
					fields = Stream.concat(Arrays.stream(parentFields), fields);
				}
			}

			this.fields.put(type, out = fields.toArray(Field[]::new));
			return out;
		}

		/*private Map<String, Object> readFields(Class<? extends IBakedModel> type, IBakedModel instance) {
			Map<String, Object> out = new HashMap<>();

			try {
				for (Field field : findFields(type)) {
					Object fieldValue = field.get(instance);

					if (fieldValue != null) {
						out.put(field.getName(), fieldValue);
					}
				}
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException("Unable to reflectively write " + type, e);
			}

			return out;
		}*/

		@Override
		public void write(JsonWriter out, IBakedModel value) throws IOException {
			if (!active) {
				active = true;
				try {
					writeFull(out, value);
				} finally {
					active = false;
				}
			} else {
				Object location = reverseModelMap.get(value);

				if (location != null) {
					GSON.toJson(location, mapKeyType, out);
				} else {
					//This case covers multipart models which have weighted models within them
					//out.value("TODO: Recursive IBlockModel - " + value);
					out.beginArray();
					writeFull(out, value);
					out.endArray();
				}
			}
		}

		private void writeFull(JsonWriter out, IBakedModel value) throws IOException {
			out.beginObject();

			out.name("type");
			Class<? extends IBakedModel> modelClass = value.getClass();
			out.value(modelClass.getName());
			if (modelClass != DelayedModel.class) {
				Map<String, Object> filledFields = new HashMap<>();//readFields(modelClass, value);
				try {
					out.name("fields");
					out.beginObject();
					for (Field field : findFields(modelClass)) {
						Object fieldValue = field.get(value);
	
						if (fieldValue != null) {
							filledFields.put(field.getName(), fieldValue);
	
							Class<?> fieldType = fieldValue.getClass();
							if (!(fieldType.isArray() ? fieldType.getComponentType() : fieldType).isPrimitive()) {
								out.name(field.getName());
								out.value(fieldType.getName());
							}
						}
					}
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException("Unable to reflectively write " + modelClass, e);
				}
				out.endObject();
				out.name("blob");
				out.beginObject();
				for (Entry<String, Object> entry : filledFields.entrySet()) {
					out.name(entry.getKey());
					Object fieldValue = entry.getValue();
					GSON.toJson(fieldValue, fieldValue.getClass(), out);
				}
				out.endObject();
			} else {
				out.name("actual");
				GSON.toJson(((DelayedModel) value).real, mapKeyType, out);
			}

			out.endObject();
		}

		@Override
		public IBakedModel read(JsonReader in) throws IOException {
			if (!active) {
				active = true;
				try {
					return readFull(in);
				} finally {
					active = false;
				}
			} else if (in.peek() == JsonToken.BEGIN_ARRAY) {
				IBakedModel activeModel = this.activeModel;
				Field activeField = this.activeField;
				try {
					in.beginArray();
					IBakedModel out = readFull(in);
					in.endArray();
					return out;
				} finally {
					this.activeField = activeField;
					this.activeModel = activeModel;
				}
			} else {
				Object location = GSON.fromJson(in, mapKeyType);
				fieldsToFix.computeIfAbsent(activeField, k -> new ReferenceOpenHashSet<>()).add(activeModel);
				return new DelayedModel(location);
			}
		}

		private IBakedModel readFull(JsonReader in) throws IOException {
			in.beginObject();

			String name = in.nextName();
			String type;
			if ("type".equals(name)) {
				type = in.nextString();
			} else {
				throw new UnsupportedOperationException("TODO: Read out of order names: " + name);
			}

			Class<? extends IBakedModel> clazz;
			try {
				clazz = Class.forName(type).asSubclass(IBakedModel.class);				
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException("Unable to reflectively create " + type, e);
			}

			IBakedModel out;
			if (clazz != DelayedModel.class) {
				@SuppressWarnings("unchecked") //We'll be careful
				Special<? extends IBakedModel> special = (Special<? extends IBakedModel>) SPECIAL_CASES.get(clazz);
				out = special != null && special.hasMaker() ? special.make(clazz) : UnsafeHacks.newInstance(clazz); //Thanks cpw

				name = in.nextName();
				if (!"fields".equals(name)) {
					throw new UnsupportedOperationException("TODO: Read out of order names: " + name);
				}

				in.beginObject();
				Map<String, Field> fieldMap = Arrays.stream(findFields(clazz)).collect(Collectors.toMap(Field::getName, Function.identity()));
				Map<Field, Type> fieldTypeMap = new Object2ReferenceOpenHashMap<>();
				while (in.hasNext()) {
					String fieldName = in.nextName();
					Field field = fieldMap.get(fieldName);
					if (field == null) throw new IllegalArgumentException("Unexpected field name " + fieldName + " for " + type);
	
					String fieldType = in.nextString(); //Actual instance type of the field
					try {
						Class<?> fieldClass = Class.forName(fieldType); //Skip noting the types of baked models
						//if (!IBakedModel.class.isAssignableFrom(fieldClass.isArray() ? fieldClass.getComponentType() : fieldClass)) fieldTypeMap.put(field, fieldClass);
						if (IModelCTM.class.isAssignableFrom(fieldClass)) fieldTypeMap.put(field, fieldClass);
					} catch (ClassNotFoundException e) {//Not the best of signs
						System.err.println("Field " + field + " has unrecognised type: " + fieldType);
					}
				}
				in.endObject();

				name = in.nextName();
				if (!"blob".equals(name)) {
					throw new UnsupportedOperationException("Unexpected name: " + name);
				}

				in.beginObject();
				try {
					while (in.hasNext()) {
						String fieldName = in.nextName();
						Field field = fieldMap.get(fieldName);
						if (field == null) throw new IllegalArgumentException("Unexpected field name " + fieldName + " for " + type);

						boolean isFinal = Modifier.isFinal(field.getModifiers());
						FieldType fieldType = FieldType.of(field);
						switch (fieldType) {
						case BOOLEAN: {
							boolean value = in.nextBoolean();
							if (isFinal) {
								UnsafeHacks.setBooleanField(field, out, value);
							} else {
								field.setBoolean(out, value);
							}
							break;
						}

						case BYTE: {
							byte value = (byte) in.nextInt();
							if (isFinal) {
								UnsafeHacks.setByteField(field, out, value);
							} else {
								field.setByte(out, value);
							}
							break;
						}

						case CHAR: {
							String fullValue = in.nextString();
							if (fullValue == null || fullValue.isEmpty()) throw new JsonSyntaxException("Expecting character, got: " + fullValue);
	
							char value = fullValue.charAt(0);
							if (isFinal) {
								UnsafeHacks.setCharField(field, out, value);
							} else {
								field.setChar(out, value);
							}
							break;
						}

						case SHORT: {
							short value = (short) in.nextInt();
							if (isFinal) {
								UnsafeHacks.setShortField(field, out, value);
							} else {
								field.setShort(out, value);
							}
							break;
						}

						case INT: {
							int value = in.nextInt();
							if (isFinal) {
								UnsafeHacks.setIntField(field, out, value);
							} else {
								field.setInt(out, value);
							}
							break;
						}

						case LONG: {
							long value = in.nextLong();
							if (isFinal) {
								UnsafeHacks.setLongField(field, out, value);
							} else {
								field.setLong(out, value);
							}
							break;
						}

						case FLOAT: {
							float value = (float) in.nextDouble();
							if (isFinal) {
								UnsafeHacks.setFloatField(field, out, value);
							} else {
								field.setFloat(out, value);
							}
							break;
						}

						case DOUBLE: {
							double value = in.nextDouble();
							if (isFinal) {
								UnsafeHacks.setDoubleField(field, out, value);
							} else {
								field.setDouble(out, value);
							}
							break;
						}

						case OBJECT: {
							//Hopefully this will be close enough to the real field's type
							Object value;
							try {
								activeModel = out;
								activeField = field;
								value = GSON.fromJson(in, fieldTypeMap.getOrDefault(field, field.getGenericType()));
							} finally {
								activeField = null;
								activeModel = null;
							}
							if (isFinal) {
								UnsafeHacks.setField(field, out, value);
							} else {
								field.set(out, value);
							}
							break;
						}

						default:
							throw new AssertionError("Unexpected field type: " + fieldType + " for " + type + '#' + fieldName);
						}
					}
				} catch (ReflectiveOperationException | UnsupportedOperationException e) {
					throw new RuntimeException("Unable to reflectively fill " + type, e);
				}
				in.endObject();
			} else {
				name = in.nextName();
				if (!"actual".equals(name)) {
					throw new UnsupportedOperationException("Unexpected name: " + name);
				}

				out = new DelayedModel(GSON.fromJson(in, mapKeyType));
			}

			in.endObject();
			return out;
		}
	}.nullSafe()).registerTypeAdapter(Map.class, new InstanceCreator<Map<?, ?>>() {
		private boolean isComparedIdentically(Class<?> type) {
			return IBakedModel.class == type;
		}

		@Override
		public Map<?, ?> createInstance(Type type) {
			TypeToken<?> keyType, valueType;
			if (type instanceof ParameterizedType) {
				Type[] types = ((ParameterizedType) type).getActualTypeArguments();
				keyType = TypeToken.of(types[0]);
				valueType = TypeToken.of(types[1]);
			} else {
				keyType = valueType = TypeToken.of(Object.class);
			}

			Class<?> key = keyType.getRawType();
			Class<?> value = valueType.getRawType();

			//If the map is using an enum as a key then EnumMap is probably the best option
			if (Enum.class.isAssignableFrom(key)) {
				return new EnumMap<>(key.asSubclass(Enum.class));
			}

			//Special case the optimisation map to retain the written order
			if ((key == ModelKey.class || key == Triple.class) && value == JsonObject.class) {
				return new Object2ObjectLinkedOpenHashMap<>();
			}
 
			switch (FieldType.of(Primitives.unwrap(key))) {
			case BOOLEAN:
				throw new IllegalArgumentException("Who makes a boolean keyed map? " + type);

			case BYTE:
				switch (FieldType.of(Primitives.unwrap(value))) {
				case BOOLEAN:
					return new Byte2BooleanOpenHashMap();

				case BYTE:
					return new Byte2ByteOpenHashMap();

				case CHAR:
					return new Byte2CharOpenHashMap();

				case SHORT:
					return new Byte2ShortOpenHashMap();

				case INT:
					return new Byte2IntOpenHashMap();

				case LONG:
					return new Byte2LongOpenHashMap();

				case FLOAT:
					return new Byte2FloatOpenHashMap();

				case DOUBLE:
					return new Byte2DoubleOpenHashMap();

				case OBJECT:
				default:
					return isComparedIdentically(value) ? new Byte2ReferenceOpenHashMap<Object>() : new Byte2ObjectOpenHashMap<Object>();
				}

			case CHAR:
				switch (FieldType.of(Primitives.unwrap(value))) {
				case BOOLEAN:
					return new Char2BooleanOpenHashMap();

				case BYTE:
					return new Char2ByteOpenHashMap();

				case CHAR:
					return new Char2CharOpenHashMap();

				case SHORT:
					return new Char2ShortOpenHashMap();

				case INT:
					return new Char2IntOpenHashMap();

				case LONG:
					return new Char2LongOpenHashMap();

				case FLOAT:
					return new Char2FloatOpenHashMap();

				case DOUBLE:
					return new Char2DoubleOpenHashMap();

				case OBJECT:
				default:
					return isComparedIdentically(value) ? new Char2ReferenceOpenHashMap<Object>() : new Char2ObjectOpenHashMap<Object>();
				}

			case SHORT:
				switch (FieldType.of(Primitives.unwrap(value))) {
				case BOOLEAN:
					return new Short2BooleanOpenHashMap();

				case BYTE:
					return new Short2ByteOpenHashMap();

				case CHAR:
					return new Short2CharOpenHashMap();

				case SHORT:
					return new Short2ShortOpenHashMap();

				case INT:
					return new Short2IntOpenHashMap();

				case LONG:
					return new Short2LongOpenHashMap();

				case FLOAT:
					return new Short2FloatOpenHashMap();

				case DOUBLE:
					return new Short2DoubleOpenHashMap();

				case OBJECT:
				default:
					return isComparedIdentically(value) ? new Short2ReferenceOpenHashMap<Object>() : new Short2ObjectOpenHashMap<Object>();
				}

			case INT:
				switch (FieldType.of(Primitives.unwrap(value))) {
				case BOOLEAN:
					return new Int2BooleanOpenHashMap();

				case BYTE:
					return new Int2ByteOpenHashMap();

				case CHAR:
					return new Int2CharOpenHashMap();

				case SHORT:
					return new Int2ShortOpenHashMap();

				case INT:
					return new Int2IntOpenHashMap();

				case LONG:
					return new Int2LongOpenHashMap();

				case FLOAT:
					return new Int2FloatOpenHashMap();

				case DOUBLE:
					return new Int2DoubleOpenHashMap();

				case OBJECT:
				default:
					return isComparedIdentically(value) ? new Int2ReferenceOpenHashMap<Object>() : new Int2ObjectOpenHashMap<Object>();
				}

			case LONG:
				switch (FieldType.of(Primitives.unwrap(value))) {
				case BOOLEAN:
					return new Long2BooleanOpenHashMap();

				case BYTE:
					return new Long2ByteOpenHashMap();

				case CHAR:
					return new Long2CharOpenHashMap();

				case SHORT:
					return new Long2ShortOpenHashMap();

				case INT:
					return new Long2IntOpenHashMap();

				case LONG:
					return new Long2LongOpenHashMap();

				case FLOAT:
					return new Long2FloatOpenHashMap();

				case DOUBLE:
					return new Long2DoubleOpenHashMap();

				case OBJECT:
				default:
					return isComparedIdentically(value) ? new Long2ReferenceOpenHashMap<Object>() : new Long2ObjectOpenHashMap<Object>();
				}

			case FLOAT:
				switch (FieldType.of(Primitives.unwrap(value))) {
				case BOOLEAN:
					return new Float2BooleanOpenHashMap();

				case BYTE:
					return new Float2ByteOpenHashMap();

				case CHAR:
					return new Float2CharOpenHashMap();

				case SHORT:
					return new Float2ShortOpenHashMap();

				case INT:
					return new Float2IntOpenHashMap();

				case LONG:
					return new Float2LongOpenHashMap();

				case FLOAT:
					return new Float2FloatOpenHashMap();

				case DOUBLE:
					return new Float2DoubleOpenHashMap();

				case OBJECT:
				default:
					return isComparedIdentically(value) ? new Float2ReferenceOpenHashMap<Object>() : new Float2ObjectOpenHashMap<Object>();
				}

			case DOUBLE:
				switch (FieldType.of(Primitives.unwrap(value))) {
				case BOOLEAN:
					return new Double2BooleanOpenHashMap();

				case BYTE:
					return new Double2ByteOpenHashMap();

				case CHAR:
					return new Double2CharOpenHashMap();

				case SHORT:
					return new Double2ShortOpenHashMap();

				case INT:
					return new Double2IntOpenHashMap();

				case LONG:
					return new Double2LongOpenHashMap();

				case FLOAT:
					return new Double2FloatOpenHashMap();

				case DOUBLE:
					return new Double2DoubleOpenHashMap();

				case OBJECT:
				default:
					return isComparedIdentically(value) ? new Double2ReferenceOpenHashMap<Object>() : new Double2ObjectOpenHashMap<Object>();
				}

			case OBJECT:
			default: {
				boolean reference = isComparedIdentically(key);

				switch (FieldType.of(Primitives.unwrap(value))) {
				case BOOLEAN:
					return reference ? new Reference2BooleanOpenHashMap<Object>() : new Object2BooleanOpenHashMap<Object>();

				case BYTE:
					return reference ? new Reference2ByteOpenHashMap<Object>() : new Object2ByteOpenHashMap<Object>();

				case CHAR:
					return reference ? new Reference2CharOpenHashMap<Object>() : new Object2CharOpenHashMap<Object>();

				case SHORT:
					return reference ? new Reference2ShortOpenHashMap<Object>() : new Object2ShortOpenHashMap<Object>();

				case INT:
					return reference ? new Reference2IntOpenHashMap<Object>() : new Object2IntOpenHashMap<Object>();

				case LONG:
					return reference ? new Reference2LongOpenHashMap<Object>() : new Object2LongOpenHashMap<Object>();

				case FLOAT:
					return reference ? new Reference2FloatOpenHashMap<Object>() : new Object2FloatOpenHashMap<Object>();

				case DOUBLE:
					return reference ? new Reference2DoubleOpenHashMap<Object>() : new Object2DoubleOpenHashMap<Object>();

				case OBJECT:
				default:
					if (reference) {
						return isComparedIdentically(value) ? new Reference2ReferenceOpenHashMap<Object, Object>() : new Reference2ObjectOpenHashMap<Object, Object>();
					} else {
						return isComparedIdentically(value) ? new Object2ReferenceOpenHashMap<Object, Object>() : new Object2ObjectOpenHashMap<Object, Object>();
					}
				}
			}
			}
		}
	})/*.registerTypeAdapter(Int2ObjectMap.class, new InstanceCreator<Int2ObjectMap<?>>() {
		@Override
		public Int2ObjectMap<?> createInstance(Type type) {
			return new Int2ObjectOpenHashMap<Object>();
		}
	})*/.registerTypeAdapterFactory(new TypeAdapterFactory() {
		private <T extends Enum<T>> TypeAdapter<T> forEnum(Class<T> type) {
			Map<String, T> nameToConstant = new Object2ReferenceOpenHashMap<>();
			Map<T, String> constantToName = new EnumMap<>(type);

			for (T constant : type.getEnumConstants()) {
				String name = constant.name();

				try {
					SerializedName annotation = type.getField(name).getAnnotation(SerializedName.class);
					if (annotation != null) {
						name = annotation.value();

						for (String alternate : annotation.alternate()) {
							nameToConstant.put(alternate, constant);
						}
					}
				} catch (NoSuchFieldException e) {
					//This happens for enum constants which have been added from IExtensibleEnum
					assert IExtensibleEnum.class.isAssignableFrom(type);
					assert constant instanceof IExtensibleEnum;
				}

				nameToConstant.put(name, constant);
				constantToName.put(constant, name);
			}

			return new TypeAdapter<T>() {
				@Override
				public void write(JsonWriter out, T value) throws IOException {
					out.value(constantToName.get(value));
				}

				@Override
				public T read(JsonReader in) throws IOException {
					return nameToConstant.get(in.nextString());
				}
			}.nullSafe();
		}

		@Override
		@SuppressWarnings("unchecked") //Not quite type safe (although only just)
		public <T> TypeAdapter<T> create(Gson gson, com.google.gson.reflect.TypeToken<T> type) {
			Class<? super T> rawType = type.getRawType();
			if (!Enum.class.isAssignableFrom(rawType)) {
				assert rawType != Enum.class;
				return null;
			}

			if (!rawType.isEnum()) {
				rawType = rawType.getSuperclass();
			}
			return forEnum(rawType.asSubclass(Enum.class));
		}
	}).disableHtmlEscaping().enableComplexMapKeySerialization().serializeSpecialFloatingPointValues().setPrettyPrinting().create();

	private static Map<Class<?>, Special<?>> specialCases() {
		Map<Class<?>, Special<?>> out = new IdentityHashMap<>();

		if (ModList.get().isLoaded("ctm")) {
			out.put(AbstractCTMBakedModel.class, new Special<AbstractCTMBakedModel>(AbstractCTMBakedModel.class, (Field field) -> {
				switch (field.getName()) {
				case "model":
				case "parent":
					return true;

				default:
					return false;
				}
			}));
			out.put(ModelBakedCTM.class, new Special<ModelBakedCTM>(ModelBakedCTM.class, (Type type) -> {
				return new ModelBakedCTM(Fake.INSTANCE, Fake.INSTANCE);
			}));
			out.put(ModelCTM.class, new Special<ModelCTM>(ModelCTM.class, typeAdapter -> {
				typeAdapter.accept(ModelCTM.class, new TypeAdapter<ModelCTM>() {
					@SuppressWarnings("serial")
					private final Type overridesType = new TypeToken<Map<Integer, JsonElement>>() {
					}.getType();

					@Override
					public void write(JsonWriter out, ModelCTM value) throws IOException {
						out.beginObject();

						BlockModel model = ((ModelCTMAccess) value).getModelinfo();
						if (model == null) {
							out.name("vanillaModel");
							GSON.toJson(((ModelCTMAccess) value).getVanillamodel(), IUnbakedModel.class, out);
						} else {
							out.name("modelInfo");
							GSON.toJson(model, BlockModel.class, out);
							out.name("overrides");
							GSON.toJson(((ModelCTMAccess) value).getOverrides(), overridesType, out);
						}

						out.endObject();
					}

					@Override
					public ModelCTM read(JsonReader in) throws IOException {
						in.beginObject();

						IUnbakedModel vanillaModel = null;
						BlockModel modelInfo = null;
						Int2ObjectMap<JsonElement> overrides = null;

						while (in.peek() != JsonToken.END_OBJECT) {
							String name = in.nextName();

							switch (name) {
							case "vanillaModel":
								assert modelInfo == null;
								assert overrides == null;
								vanillaModel = GSON.fromJson(in, IUnbakedModel.class);
								break;

							case "modelInfo":
								assert vanillaModel == null;
								modelInfo = GSON.fromJson(in, BlockModel.class);
								break;

							case "overrides":
								assert vanillaModel == null;
								overrides = GSON.fromJson(in, overridesType);
								break;
							}
						}

						in.endObject();
						ModelCTM out = modelInfo == null ? new ModelCTM(vanillaModel) : new ModelCTM(modelInfo, overrides);
						out.initializeTextures(ModelLoader.instance(), ModelLoader.defaultTextureGetter());
						return out;
					}
				});
			}));
		}

		return out;
	}

	private static GsonBuilder builder() {
		GsonBuilder out = new GsonBuilder();

		for (Special<?> specialCase : SPECIAL_CASES.values()) {
			specialCase.appendExtraTypes(out::registerTypeAdapter);
			if (specialCase.hasMaker()) {
				out.registerTypeAdapter(specialCase.name, specialCase.maker());
			}
		}

		return out;
	}

	static void test() {
		//Set<E>.class;
		//EnumSet<Enum<E>>.class
		//ResourceLocationException.class
		//InstanceCreator<T>
		//IUnbakedModel
		//Predicate<//T>
		//Int2ObjectMap<V>
		//JsonElement
		//IModelCTM.class
		//Object2ReferenceLinkedOpenHashMap<K, V>.class
		//IExtensibleEnum.class
		new InstanceCreator<Map<?, ?>>() {
			@Override
			public Map<?, ?> createInstance(Type fullMapType) {/*
		new TypeAdapterFactory() {
			@Override
			public <T> TypeAdapter<T> create(Gson gson, com.google.gson.reflect.TypeToken<T> type) {
				Class<? super T> rawType = type.getRawType();
				if (!Map.class.isAssignableFrom(rawType)) return null;

				@SuppressWarnings("unchecked") //Converting TypeToken to TypeToken
				TypeToken<? extends Map<?, ?>> sameType = (TypeToken<? extends Map<?, ?>>) TypeToken.of(type.getType());
				Type fullMapType = sameType.getSupertype(Map.class).getType();*/

				Type keyType, valueType;
				/*if (!EnumMap.class.isAssignableFrom(rawType)) {
					//Can we work out the key type?
					if (!(fullMapType instanceof ParameterizedType)) return null;

					Type[] types = ((ParameterizedType) fullMapType).getActualTypeArguments();
					keyType = types[0];
					if (Enum.class.isAssignableFrom(TypeToken.of(keyType).getRawType())) return null;
					valueType = types[1];
				} else {*/
					if (fullMapType instanceof ParameterizedType) {
						Type[] types = ((ParameterizedType) fullMapType).getActualTypeArguments();
						keyType = types[0];
						valueType = types[1];
					} else {
						keyType = valueType = Object.class;
					}
				//}

				final Type type = fullMapType;
				//gson.getAdapter(com.google.gson.reflect.TypeToken.get(valueType));
				System.out.printf("Unexpected map types: %s and %s (%s and %s) for %s%n", keyType, valueType,
						Arrays.toString(keyType.getClass().getInterfaces()), Arrays.toString(valueType.getClass().getInterfaces()), type);
				return new Object2ObjectLinkedOpenHashMap<Object, Object>();
			}
		};
	}

	public static void serialise(Map<?, IBakedModel> models, Map<ResourceLocation, IBakedModel> topModels, Path to) {
		Map<Object, IBakedModel> allModels = new Object2ReferenceLinkedOpenHashMap<>(models.size());

		assert reverseModelMap.isEmpty();
		for (Entry<ResourceLocation, IBakedModel> entry : topModels.entrySet()) {//Loop top models first as mods can replace these via ModelBakeEvent
			Object key = FoamyConfig.THREAD_MODELS.asBoolean() ? new ModelKey(entry.getKey(), ModelRotation.X0_Y0.getRotation(), ModelRotation.X0_Y0.isUvLock())
																: Triple.of(entry.getKey(), ModelRotation.X0_Y0.getRotation(), ModelRotation.X0_Y0.isUvLock());

			IBakedModel model = entry.getValue();
			if (!reverseModelMap.containsKey(model)) {
				allModels.put(key, model);
				reverseModelMap.put(model, key);
			} else {
				allModels.put(key, new DelayedModel(reverseModelMap.get(model)));
			}
		}
		for (Entry<?, IBakedModel> entry : models.entrySet()) {
			if (!allModels.containsKey(entry.getKey())) {
				IBakedModel model = entry.getValue();

				if (!reverseModelMap.containsKey(model)) {
					allModels.put(entry.getKey(), model);
					reverseModelMap.put(model, entry.getKey());
				} else {
					allModels.put(entry.getKey(), new DelayedModel(reverseModelMap.get(model)));
				}
			}
		}

		try (Writer out = Files.newBufferedWriter(to)) {
			GSON.toJson(allModels, MAP_TYPE, out);
		} catch (IOException e) {
			throw new RuntimeException("Error writing " + allModels.size() + " (" + models.size() + " + " + topModels.size() + ") to " + to, e);
		} finally {
			reverseModelMap.clear();
		}
	}

	public static void optimise(Path from) {
		@SuppressWarnings("serial")
		Type mapType = (FoamyConfig.THREAD_MODELS.asBoolean() ? new TypeToken<Map<ModelKey, JsonObject>>() {
		} : new TypeToken<Map<Triple<ResourceLocation, TransformationMatrix, Boolean>, JsonObject>>() {
		}).getType();

		Map<?, JsonObject> models;
		try (Reader in = Files.newBufferedReader(from)) {
			models = GSON.fromJson(in, mapType);
		} catch (IOException e) {
			throw new RuntimeException("Error reading models from " + from, e);
		}

		Map<JsonObject, Object> uniqueModels = new Object2ObjectOpenHashMap<>(models.size());
		Map<Object, Object> swaps = new Object2ObjectOpenHashMap<>();

		final JsonPrimitive delayedType = new JsonPrimitive(DelayedModel.class.getName());
		for (Entry<?, JsonObject> entry : models.entrySet()) {
			JsonObject model = entry.getValue();

			if (model.size() == 2 && delayedType.equals(model.getAsJsonPrimitive("type")) && model.has("actual")) {//DelayedModels are already as simple as they need to be
				@SuppressWarnings("serial")
				Type mapKeyType = FoamyConfig.THREAD_MODELS.asBoolean() ? ModelKey.class : new TypeToken<Triple<ResourceLocation, TransformationMatrix, Boolean>>() {
				}.getType();
				Object key = GSON.fromJson(model.get("actual"), mapKeyType);

				Object newKey = swaps.get(key);
				if (newKey != null) {
					//System.out.println("Swapping " + key + " to " + newKey);
					model.add("actual", GSON.toJsonTree(newKey, mapKeyType));
				} else if (!uniqueModels.containsValue(key)) {
					System.err.println(entry.getKey() + " depends on " + key + " before it is defined!");
				}
			} else {
				Object newKey = uniqueModels.get(model);//Have we already seen this (actual) model?

				if (newKey == null) {//No, note the model down
					uniqueModels.put(model, entry.getKey());
				} else {//Yes, reuse
					swaps.put(entry.getKey(), newKey);
					entry.setValue(GSON.toJsonTree(new DelayedModel(newKey), IBakedModel.class).getAsJsonObject());
				}
			}
		}

		System.out.printf("Of %d models, %d are unnecessary duplicates%n", models.size(), swaps.size());
		try (Writer out = Files.newBufferedWriter(from)) {
			GSON.toJson(models, mapType, out);
		} catch (IOException e) {
			throw new RuntimeException("Error writing " + uniqueModels.size() + " (from " + models.size() + ") to " + from, e);
		}
	}

	private static IBakedModel fetchModel(Map<?, IBakedModel> models, Object location) {
		IBakedModel model = models.get(location);
		if (model == null) {
			System.err.println("Unable to find requested model: " + location);
			return null; //Oh dear
		}

		return model;
	}

	public static Map<?, IBakedModel> deserialise(Path from) {
		try (Reader in = Files.newBufferedReader(from)) {
			Map<?, IBakedModel> out = GSON.fromJson(in, MAP_TYPE);

			for (Entry<?, IBakedModel> entry : out.entrySet()) {
				IBakedModel model = entry.getValue();

				/*while (model instanceof DelayedModel || model == null) {
					Object key = ((DelayedModel) model).real;
					if (entry.getKey().equals(key)) {
						throw new IllegalStateException("Needed model which itself is needed: " + key);
					}

					model = out.get(key);
					if (model == null && !out.containsKey(key)) {
						System.err.println("Lost reused model: " + key + '?');
						//entry.setValue(null); //It's null anyway...
					}
				}

				entry.setValue(model);*/
				if (model instanceof DelayedModel) {
					Object key = ((DelayedModel) model).real;
					model = out.get(key);

					if (model == null && !out.containsKey(key)) {
						System.err.println("Lost reused model: " + key + '?');
						entry.setValue(null); //It's null anyway...
					} else if (model instanceof DelayedModel) {
						throw new IllegalStateException("Needed model which itself is needed: " + key);
					} else {
						entry.setValue(model);
					}
				}
			}

			for (Entry<Field, Set<IBakedModel>> entry : fieldsToFix.entrySet()) {
				Field field = entry.getKey();
				Class<?> rawFieldType = field.getType();

				if (IBakedModel.class.isAssignableFrom(rawFieldType)) {
					for (IBakedModel instance : entry.getValue()) {
						try {
							DelayedModel model = (DelayedModel) field.get(instance);
							IBakedModel real = fetchModel(out, model.real);
	
							if (Modifier.isFinal(field.getModifiers())) {
								UnsafeHacks.setField(field, instance, real);
							} else {
								field.set(instance, real);
							}
						} catch (ReflectiveOperationException | ClassCastException e) {
							Object key = "<unknown>";
							for (Entry<?, IBakedModel> innerEntry : out.entrySet()) {
								if (innerEntry.getValue() == instance) {
									key = innerEntry.getKey();
									break;
								}
							}
							throw new RuntimeException("Failed to fill " + field + " in " + instance + " (" + key + ')', e);
						}
					}
					continue;
				} else if (IBakedModel[].class.isAssignableFrom(rawFieldType)) {
					for (IBakedModel instance : entry.getValue()) {
						try {
							IBakedModel[] models = (IBakedModel[]) field.get(instance);
	
							for (int i = 0, end = models.length; i < end; i++) {
								if (models[i] instanceof DelayedModel) {
									models[i] = fetchModel(out, ((DelayedModel) models[i]).real);
								}
							}
						} catch (ReflectiveOperationException | ClassCastException | ArrayStoreException e) {
							Object key = "<unknown>";
							for (Entry<?, IBakedModel> innerEntry : out.entrySet()) {
								if (innerEntry.getValue() == instance) {
									key = innerEntry.getKey();
									break;
								}
							}
							throw new RuntimeException("Failed to fill " + field + " in " + instance + " (" + key + ')', e);
						}
					}
					continue;
				} else if (Collection.class.isAssignableFrom(rawFieldType)) {
					@SuppressWarnings("unchecked")
					TypeToken<? extends Collection<?>> fieldType = (TypeToken<? extends Collection<?>>) TypeToken.of(field.getGenericType());
					Type fullFieldType = fieldType.getSupertype(Collection.class).getType();

					Type collectionType;
					if (fullFieldType instanceof ParameterizedType) {
						collectionType = ((ParameterizedType) fullFieldType).getActualTypeArguments()[0];
					} else if (fullFieldType instanceof WildcardType) {
						collectionType = ((WildcardType) fullFieldType).getUpperBounds()[0];
					} else {
						System.err.println("Unable to find type of collection for " + field);
						continue;
					}

					if (collectionType instanceof Class<?>) {
						if (IBakedModel.class.isAssignableFrom((Class<?>) collectionType)) {
							for (IBakedModel instance : entry.getValue()) {
								try {
									@SuppressWarnings("unchecked") //Checked using the logic above
									Collection<IBakedModel> models = (Collection<IBakedModel>) field.get(instance);
									List<IBakedModel> replacement = new ArrayList<>(models.size());
	
									for (IBakedModel model : models) {
										replacement.add(model instanceof DelayedModel ? fetchModel(out, ((DelayedModel) model).real) : model);
									}
									models.clear(); //How badly can it go?
									models.addAll(replacement);
								} catch (ReflectiveOperationException | ClassCastException | UnsupportedOperationException e) {
									Object key = "<unknown>";
									for (Entry<?, IBakedModel> innerEntry : out.entrySet()) {
										if (innerEntry.getValue() == instance) {
											key = innerEntry.getKey();
											break;
										}
									}
									throw new RuntimeException("Failed to fill " + field + " in " + instance + " (" + key + ')', e);
								}
							}
							continue;
						} else if ("net.minecraft.client.renderer.model.WeightedBakedModel$WeightedModel".equals(collectionType.getTypeName())) {
							Field innerField;
							out: try {
								for (Field maybeField : Class.forName("net.minecraft.client.renderer.model.WeightedBakedModel$WeightedModel").getDeclaredFields()) {
									if (maybeField.getType() == IBakedModel.class) {
										innerField = maybeField;
										break out;
									}
								}

								throw new NoSuchFieldException("Can't find field in WeightedModel!");
							} catch (ReflectiveOperationException e) {
								throw new RuntimeException("Error finding field in WeightedModel", e);
							}

							for (IBakedModel instance : entry.getValue()) {
								try {
									Collection<?> models = (Collection<?>) field.get(instance);

									for (Object weightedModel : models) {
										IBakedModel model = (IBakedModel) innerField.get(weightedModel);
										if (!(model instanceof DelayedModel)) continue;

										IBakedModel real = fetchModel(out, ((DelayedModel) model).real);
										if (Modifier.isFinal(innerField.getModifiers())) {
											UnsafeHacks.setField(innerField, weightedModel, real);
										} else {
											innerField.set(weightedModel, real);
										}
									}
								} catch (ReflectiveOperationException | ClassCastException e) {
									Object key = "<unknown>";
									for (Entry<?, IBakedModel> innerEntry : out.entrySet()) {
										if (innerEntry.getValue() == instance) {
											key = innerEntry.getKey();
											break;
										}
									}
									throw new RuntimeException("Failed to fill " + innerField + " in " + instance + " (" + key + ')', e);
								}
							}
							continue;
						}
					} else {
						System.err.println("Unexpected collection type: " + collectionType + " (" + collectionType.getClass() + ')');
					}
				} else if (Map.class.isAssignableFrom(rawFieldType)) {
					@SuppressWarnings("unchecked")
					TypeToken<? extends Map<?, ?>> fieldType = (TypeToken<? extends Map<?, ?>>) TypeToken.of(field.getGenericType());					
					Type fullFieldType = fieldType.getSupertype(Map.class).getType();

					//Can we work out the key type?
					if (!(fullFieldType instanceof ParameterizedType)) {
						System.err.println("Unable to find type of map for " + field);
						continue;
					}

					Type[] types = ((ParameterizedType) fullFieldType).getActualTypeArguments();
					Type keyType = types[0];
					Type valueType = types[1];

					throw new UnsupportedOperationException("TODO: " + field + ", a Map<" + keyType + ", " + valueType + '>');
				} else if (ItemOverrideList.class.isAssignableFrom(rawFieldType)) {
					for (IBakedModel instance : entry.getValue()) {
						try {
							ItemOverrideList overrides = (ItemOverrideList) field.get(instance);

							for (ListIterator<IBakedModel> it = ((ItemOverrideListAccess) overrides).getOverrideBakedModels().listIterator(); it.hasNext();) {
								IBakedModel model = it.next();

								if (model instanceof DelayedModel) {
									it.set(fetchModel(out, ((DelayedModel) model).real));
								}
							}
						} catch (ReflectiveOperationException | UnsupportedOperationException e) {
							Object key = "<unknown>";
							for (Entry<?, IBakedModel> innerEntry : out.entrySet()) {
								if (innerEntry.getValue() == instance) {
									key = innerEntry.getKey();
									break;
								}
							}
							throw new RuntimeException("Failed to fill " + field + " in " + instance + " (" + key + ')', e);
						}
					}
					continue;
				}

				System.err.println("TODO: Reflectively find nested IBakedModel field in " + field.toGenericString());
			}

			return out;
		} catch (IOException e) {
			throw new RuntimeException("Error reading models from " + from, e);
		} finally {
			fieldsToFix.clear();
		}
	}

	private static boolean equal(TextureAtlasSprite a, TextureAtlasSprite b) {
		if (a == b) return true;
		if (a == null || b == null) return false;

		return Objects.equals(a.getName(), b.getName()) && Objects.equals(a.getAtlasTexture().getTextureLocation(), b.getAtlasTexture().getTextureLocation());
	}

	private static boolean equal(ResourceLocation model, BooleanSupplier expected, BooleanSupplier actual) {
		boolean expectedResult;
		try {
			expectedResult = expected.getAsBoolean();
		} catch (Throwable t) {
			System.err.println("The model for " + model + " was unimpressed");
			return true; //Whatever
		}

		return expectedResult == actual.getAsBoolean();
	}

	private static boolean equal(Random random, BlockState state, ResourceLocation model, IBakedModel expectedModel, IBakedModel actualModel) {
		try {
			return equalUnchecked(random, state, model, expectedModel, actualModel);
		} catch (Throwable t) {
			t.printStackTrace();
			return false; //If the expected didn't throw the actual shouldn't either
		}
	}

	private static boolean equalUnchecked(Random random, BlockState state, ResourceLocation model, IBakedModel expectedModel, IBakedModel actualModel) {
		for (Direction side : Direction.values()) {
			List<BakedQuad> expectedQuads;
			try {
				random.setSeed(42);
				expectedQuads = expectedModel.getQuads(state, side, random, EmptyModelData.INSTANCE);
			} catch (Throwable t) {
				System.err.println("The model for " + model + " was unimpressed");
				t.printStackTrace();
				continue;
			}

			random.setSeed(42);
			List<BakedQuad> actualQuads = actualModel.getQuads(state, side, random, EmptyModelData.INSTANCE);

			if (expectedQuads == null) {
				if (actualQuads != null) {
					return actualQuads.isEmpty();
				} else {
					continue;
				}
			} else if (actualQuads == null) {
				return expectedQuads.isEmpty(); //We made null quads apparently
			}

			if (Streams.zip(expectedQuads.stream(), actualQuads.stream(), (expected, actual) -> {
				return !Arrays.equals(expected.getVertexData(), actual.getVertexData()) || expected.hasTintIndex() != actual.hasTintIndex() || expected.getTintIndex() != actual.getTintIndex()
						|| expected.getFace() != actual.getFace() || !equal(expected.getSprite(), actual.getSprite()) || expected.applyDiffuseLighting() != actual.applyDiffuseLighting();
			}).anyMatch(Boolean::booleanValue)) {
				return false;
			}
		}

		if (!equal(model, () -> expectedModel.isAmbientOcclusion(state), () -> actualModel.isAmbientOcclusion(state))
				|| !equal(model, () -> expectedModel.isGui3d(), () -> actualModel.isGui3d())
				|| !equal(model, () -> expectedModel.isSideLit(), () -> actualModel.isSideLit())
				|| !equal(model, () -> expectedModel.doesHandlePerspectives(), () -> actualModel.doesHandlePerspectives())
				|| !equal(model, () -> expectedModel.isLayered(), () -> actualModel.isLayered())
				|| !equal(model, () -> expectedModel.isBuiltInRenderer(), () -> actualModel.isBuiltInRenderer())) {
			return false;
		}

		TextureAtlasSprite expectedSprite;
		try {
			expectedSprite = expectedModel.getParticleTexture(EmptyModelData.INSTANCE);
		} catch (Throwable t) {
			System.err.println("The model for " + model + " was unimpressed");
			return true;
		}

		return equal(expectedSprite, actualModel.getParticleTexture(EmptyModelData.INSTANCE));
	}

	public static void compare(Map<?, IBakedModel> models) {
		Map<ResourceLocation, IBakedModel> expectedModels = ModelLoader.instance().getTopBakedModels();
		System.out.println("Validating " + expectedModels.size() + " expected models (from " + models.size() + ')');

		Random random = new Random();
		for (Block block : ForgeRegistries.BLOCKS) {
			for (BlockState state : block.getStateContainer().getValidStates()) {
				ModelResourceLocation model = BlockModelShapes.getModelLocation(state);

				IBakedModel expectedModel = expectedModels.get(model);
				if (expectedModel != null) {
					Object key = FoamyConfig.THREAD_MODELS.asBoolean() ? new ModelKey(model, ModelRotation.X0_Y0.getRotation(), ModelRotation.X0_Y0.isUvLock()) : Triple.of(model, ModelRotation.X0_Y0.getRotation(), ModelRotation.X0_Y0.isUvLock());
					//Object key = FoamyConfig.THREAD_MODELS.asBoolean() ? new ModelKey(model, TransformationMatrix.identity().getMatrix(), ModelRotation.X0_Y0.isUvLock()) : Triple.of(model, ModelRotation.X0_Y0.getRotation(), ModelRotation.X0_Y0.isUvLock());
					IBakedModel actualModel = models.get(key);

					if (actualModel == null) {
						System.err.println("Lost the model for " + model);
						System.err.println();
					} else if (!equal(random, state, model, expectedModel, actualModel)) {
						System.err.println("Produced different model for " + model);
						if (expectedModel.getClass() != actualModel.getClass()) System.err.println("Produced " + actualModel.getClass() + " rather than " + expectedModel.getClass());
						System.err.println();
					}
				} else {
					System.err.println("Apparently got null top model for " + model);
					System.err.println();
				}
			}
		}

		for (Item item : ForgeRegistries.ITEMS) {
			if (item != Items.AIR) {
				ModelResourceLocation model = new ModelResourceLocation(item.getRegistryName(), "inventory");

				IBakedModel expectedModel = expectedModels.get(model);
				if (expectedModel != null) {
					Object key = FoamyConfig.THREAD_MODELS.asBoolean() ? new ModelKey(model, ModelRotation.X0_Y0.getRotation(), ModelRotation.X0_Y0.isUvLock()) :
																			Triple.of(model, ModelRotation.X0_Y0.getRotation(), ModelRotation.X0_Y0.isUvLock());
					IBakedModel actualModel = models.get(key);

					if (actualModel == null) {
						System.err.println("Lost the model for " + model);
						System.err.println();
					} else if (!equal(random, null, model, expectedModel, actualModel)) {
						System.err.println("Produced different model for " + model);
						System.err.println();
					}
				} else {
					System.err.println("Apparently got null top model for " + model);
					System.err.println();
				}
			}
		}

		System.out.println("Check complete");
	}
}