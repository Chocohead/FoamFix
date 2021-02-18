package pl.asie.foamfix.blob;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import com.google.common.primitives.Primitives;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
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
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ByteOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2CharOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMaps;
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
import net.minecraft.client.renderer.model.SimpleBakedModel;
import net.minecraft.client.renderer.model.WeightedBakedModel;
import net.minecraft.client.renderer.model.WeightedBakedModel.WeightedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.common.IExtensibleEnum;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;

import team.chisel.ctm.api.model.IModelCTM;
import team.chisel.ctm.client.model.AbstractCTMBakedModel;
import team.chisel.ctm.client.model.ModelBakedCTM;
import team.chisel.ctm.client.model.ModelCTM;

import pl.asie.foamfix.FoamyCacherCleanser;
import pl.asie.foamfix.FoamyConfig;
import pl.asie.foamfix.Util;
import pl.asie.foamfix.mixin.blob.ItemOverrideListAccess;
import pl.asie.foamfix.mixin.blob.ModelBakeryAccess;
import pl.asie.foamfix.mixin.blob.ModelCTMAccess;
import pl.asie.foamfix.mixin.blob.WeightedBakedModelAccess;
import pl.asie.foamfix.multipart.ResolvedMultipart.ResolvedMultipartModel;
import pl.asie.foamfix.thready.ModelKey;

@SuppressWarnings("deprecation")
public class ModelSerialiser {
	public static final Logger LOGGER = LogManager.getLogger("FoamFix/Model-Serialisation");
	@SuppressWarnings("serial") //Bit redundant for our purposes
	private static final Type MAP_TYPE = (FoamyConfig.THREAD_MODELS.asBoolean() ? new TypeToken<Map<ModelKey, IBakedModel>>() {
	} : new TypeToken<Map<Triple<ResourceLocation, TransformationMatrix, Boolean>, IBakedModel>>() {
	}).getType();
	@SuppressWarnings("serial")
	static final Type MAP_KEY_TYPE = FoamyConfig.THREAD_MODELS.asBoolean() ? ModelKey.class : new TypeToken<Triple<ResourceLocation, TransformationMatrix, Boolean>>() {
	}.getType();
	@SuppressWarnings("serial")
	private static final Type REJECTS_TYPE = new TypeToken<Set<ResourceLocation>>() {
	}.getType();
	private static Map<IBakedModel, Object> reverseModelMap = new IdentityHashMap<>();
	private static List<AwaitingModel<?>> fieldsToFix = new ArrayList<>();
	private static final Map<Class<?>, ModelAdapter<?>> KNOWN_MODELS = specialCases();
	private static final Gson GSON = builder().registerTypeAdapter(ModelKey.class, new TypeAdapter<ModelKey>() {
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
	}.nullSafe()).registerTypeAdapterFactory(new TypeAdapterFactory() {
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
			return ((ModelBakeryAccess) (Object) ModelLoader.instance()).getSpriteMap().getAtlasTexture(atlas).getSprite(name);
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
				private final UnaryOperator<ItemOverrideList> fixer = glanceFixer(new AwaitingModelList<>(instance -> ((ItemOverrideListAccess) instance).getOverrideBakedModels())); 

				@Override
				public void write(JsonWriter out, ItemOverrideList value) throws IOException {
					if (ItemOverrideList.EMPTY == value) {
						out.value("<default>");
					} else {
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
						return fixer.apply(adapter.read(in));
					}

					default:
						throw new IllegalArgumentException("Unexpected JSON element: " + in.peek());
					}
				}
			} : null;
		}
	}).registerTypeHierarchyAdapter(IUnbakedModel.class, new TypeAdapter<IUnbakedModel>() {
		@Override
		public void write(JsonWriter out, IUnbakedModel value) throws IOException {
			for (Entry<ResourceLocation, IUnbakedModel> entry : Util.entrySet(((ModelBakeryAccess) (Object) ModelLoader.instance()).getUnbakedModels())) {
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
		private boolean active;

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
					GSON.toJson(location, MAP_KEY_TYPE, out);
				} else {
					//This case covers multipart models which have weighted models within them
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
				out.name("blob");
				@SuppressWarnings("unchecked") //Probably fine
				ModelAdapter<IBakedModel> modelAdapter = (ModelAdapter<IBakedModel>) KNOWN_MODELS.get(modelClass);
				if (modelAdapter == null) {
					throw new IllegalArgumentException("Unexpected model type: " + modelClass);
				} else {
					modelAdapter.write(out, value);
				}
			} else {
				out.name("actual");
				GSON.toJson(((DelayedModel) value).real, MAP_KEY_TYPE, out);
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
				in.beginArray();
				IBakedModel out = readFull(in);
				in.endArray();
				return out;
			} else {
				Object location = GSON.fromJson(in, MAP_KEY_TYPE);
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
				name = in.nextName();
				if (!"blob".equals(name)) {
					throw new UnsupportedOperationException("Unexpected name: " + name);
				}

				@SuppressWarnings("unchecked") //Probably fine
				ModelAdapter<IBakedModel> modelAdapter = (ModelAdapter<IBakedModel>) KNOWN_MODELS.get(clazz);
				if (modelAdapter == null) {
					throw new IllegalArgumentException("Lost model type: " + clazz);
				} else {
					out = modelAdapter.read(in);
				}
			} else {
				name = in.nextName();
				if (!"actual".equals(name)) {
					throw new UnsupportedOperationException("Unexpected name: " + name);
				}

				out = new DelayedModel(GSON.fromJson(in, MAP_KEY_TYPE));
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
	}).registerTypeAdapterFactory(new TypeAdapterFactory() {
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
			return /* ECJ doesn't need this cast: */(TypeAdapter<T>) forEnum(rawType.asSubclass(Enum.class));
		}
	}).disableHtmlEscaping().enableComplexMapKeySerialization().serializeSpecialFloatingPointValues().setPrettyPrinting().create();

	private static Map<Class<?>, ModelAdapter<?>> specialCases() {
		Map<Class<?>, ModelAdapter<?>> out = new IdentityHashMap<>();

		out.put(SimpleBakedModel.class, new ModelAdapter<SimpleBakedModel>() {
			@SuppressWarnings("serial") //Bit silly
			private final Type quadList = new TypeToken<List<BakedQuad>>() {
			}.getType();
			@SuppressWarnings("serial")
			private final Type quadMap = new TypeToken<Map<Direction, List<BakedQuad>>>() {
			}.getType();

			@Override
			public boolean valid(SimpleBakedModel model) {
				for (IBakedModel override : ((ItemOverrideListAccess) model.getOverrides()).getOverrideBakedModels()) {
					if (!canSerialise(override)) return false;
				}

				return true;
			}

			@Override
			public Set<Class<? extends IBakedModel>> blameNonvalidity(SimpleBakedModel model) {
				return ((ItemOverrideListAccess) model.getOverrides()).getOverrideBakedModels().stream()
								.filter(Predicates.not(ModelSerialiser::canSerialise)).map(IBakedModel::getClass).collect(Collectors.toSet());
			}

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
						assert faceQuads instanceof EnumMap;
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
				return new SimpleBakedModel(quads, faceQuads, usesAO, isSideLit, hasDepth, sprite, transformation, itemOverrides);
			}
		});
		out.put(BuiltInModel.class, new ModelAdapter<BuiltInModel>() {
			@Override
			public boolean valid(BuiltInModel model) {
				for (IBakedModel override : ((ItemOverrideListAccess) model.getOverrides()).getOverrideBakedModels()) {
					if (!canSerialise(override)) return false;
				}

				return true;
			}

			@Override
			public Set<Class<? extends IBakedModel>> blameNonvalidity(BuiltInModel model) {
				return ((ItemOverrideListAccess) model.getOverrides()).getOverrideBakedModels().stream()
								.filter(Predicates.not(ModelSerialiser::canSerialise)).map(IBakedModel::getClass).collect(Collectors.toSet());
			}

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
		});
		out.put(WeightedBakedModel.class, new ModelAdapter<WeightedBakedModel>() {
			private final UnaryOperator<WeightedBakedModel> fixer = glanceFixer(new ModelField<>(ObfuscationReflectionHelper.findField(WeightedBakedModel.class, "field_177566_c")));
			@SuppressWarnings("serial")
			private final Type modelsType = new TypeToken<List<WeightedModel>>() {
			}.getType();

			@Override
			public boolean valid(WeightedBakedModel model) {
				for (WeightedModel weightedModel : ((WeightedBakedModelAccess) model).getModels()) {
					if (!canSerialise(weightedModel.model)) return false;
				}

				return true;
			}

			@Override
			public Set<Class<? extends IBakedModel>> blameNonvalidity(WeightedBakedModel model) {
				return ((WeightedBakedModelAccess) model).getModels().stream().map(weightedModel -> weightedModel.model)
								.filter(Predicates.not(ModelSerialiser::canSerialise)).map(IBakedModel::getClass).collect(Collectors.toSet());
			}

			@Override
			public void write(JsonWriter out, WeightedBakedModel value) throws IOException {
				out.beginObject();

				out.name("models");
				GSON.toJson(((WeightedBakedModelAccess) value).getModels(), modelsType, out);

				out.endObject();
			}

			@Override
			public WeightedBakedModel read(JsonReader in) throws IOException {
				in.beginObject();

				String name = in.nextName();
				if (!"models".equals(name)) {
					throw new UnsupportedOperationException("Unexpected name: " + name);
				}
				List<WeightedModel> models = GSON.fromJson(in, modelsType);

				in.endObject();
				return fixer.apply(new WeightedBakedModel(models));
			}

			@Override
			protected void appendExtraTypes(BiConsumer<Type, TypeAdapter<?>> typeAdapter) {
				typeAdapter.accept(WeightedModel.class, new TypeAdapter<WeightedModel>() {
					private final UnaryOperator<WeightedModel> fixer = glanceFixer(new QuickModelField<>(ObfuscationReflectionHelper.findField(WeightedModel.class, "field_185281_b"), model -> model.model));

					@Override
					public void write(JsonWriter out, WeightedModel value) throws IOException {
						out.beginArray();

						GSON.toJson(value.model, IBakedModel.class, out);
						out.value(value.itemWeight);

						out.endArray();
					}

					@Override
					public WeightedModel read(JsonReader in) throws IOException {
						in.beginArray();

						IBakedModel model = GSON.fromJson(in, IBakedModel.class);
						int weight = in.nextInt();

						in.endArray();
						return fixer.apply(new WeightedModel(model, weight));
					}
				});
			}
		});
		out.put(ResolvedMultipartModel.class, new ModelAdapter<ResolvedMultipartModel>() {
			private final Consumer<IBakedModel[]> fixer = queueFixer(new AwaitingModelArray<>(Function.identity()));

			@Override
			public boolean valid(ResolvedMultipartModel model) {
				for (IBakedModel part : model.models) {
					if (!canSerialise(part)) return false;
				}

				return true;
			}

			@Override
			public Set<Class<? extends IBakedModel>> blameNonvalidity(ResolvedMultipartModel model) {
				return Arrays.stream(model.models).filter(Predicates.not(ModelSerialiser::canSerialise)).map(IBakedModel::getClass).collect(Collectors.toSet());
			}

			@Override
			public void write(JsonWriter out, ResolvedMultipartModel value) throws IOException {
				out.beginObject();

				out.name("models");
				GSON.toJson(value.models, IBakedModel[].class, out);

				out.endObject();
			}

			@Override
			public ResolvedMultipartModel read(JsonReader in) throws IOException {
				in.beginObject();

				String name = in.nextName();
				if (!"models".equals(name)) {
					throw new UnsupportedOperationException("Unexpected name: " + name);
				}
				IBakedModel[] models = GSON.fromJson(in, IBakedModel[].class);

				in.endObject();
				fixer.accept(models);
				return new ResolvedMultipartModel(models);
			}
		});

		if (ModList.get().isLoaded("ctm")) {
			out.put(ModelBakedCTM.class, new ModelAdapter<ModelBakedCTM>() {
				private final UnaryOperator<ModelBakedCTM> fixer = glanceFixer(new QuickModelField<ModelBakedCTM>(FieldUtils.getDeclaredField(AbstractCTMBakedModel.class, "parent", true), ModelBakedCTM::getParent));

				@Override
				public boolean valid(ModelBakedCTM model) {
					return canSerialise(model.getParent());
				}

				@Override
				public Set<Class<? extends IBakedModel>> blameNonvalidity(ModelBakedCTM model) {
					return Collections.singleton(model.getParent().getClass());
				}

				@Override
				public void write(JsonWriter out, ModelBakedCTM value) throws IOException {
					out.beginObject();

					out.name("model");
					withType(GSON, out, value.getModel());
					out.name("parent");
					GSON.toJson(value.getParent(), IBakedModel.class, out);

					out.endObject();
				}

				@Override
				public ModelBakedCTM read(JsonReader in) throws IOException {
					in.beginObject();

					IModelCTM model = null;
					IBakedModel parent = null;

					while (in.peek() != JsonToken.END_OBJECT) {
						String name = in.nextName();

						switch (name) {
						case "model":
							model = withType(GSON, in);
							break;

						case "parent":
							parent = GSON.fromJson(in, IBakedModel.class);
							break;
						}
					}

					in.endObject();
					return fixer.apply(new ModelBakedCTM(model, parent));
				}

				@Override
				protected void appendExtraTypes(BiConsumer<Type, TypeAdapter<?>> typeAdapter) {
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
							out.initializeTextures(ModelLoader.instance(), material -> ((ModelBakeryAccess) (Object) ModelLoader.instance()).getSpriteMap()
								.getAtlasTexture(material.getAtlasLocation()).getSprite(material.getTextureLocation())
							);
							return out;
						}
					});
				}
			});
		}

		return out;
	}

	static <T> UnaryOperator<T> glanceFixer(AwaitingModel<T> fixer) {
		Consumer<T> fix = queueFixer(fixer);
		return instance -> {
			fix.accept(instance);
			return instance;
		};
	}

	static <T> Consumer<T> queueFixer(AwaitingModel<T> fixer) {
		fieldsToFix.add(fixer);
		return fixer::queue;
	}

	private static GsonBuilder builder() {
		GsonBuilder out = new GsonBuilder();

		for (ModelAdapter<?> specialCase : KNOWN_MODELS.values()) {
			specialCase.appendExtraTypes(out::registerTypeAdapter);
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

	public static boolean canSerialise(IBakedModel model) {
		if (model == null) return true; //It'll get skipped but sure

		@SuppressWarnings("unchecked") //They're added to the map by type so this should be fine
		ModelAdapter<IBakedModel> serialiser = (ModelAdapter<IBakedModel>) KNOWN_MODELS.get(model.getClass());
		return serialiser != null && serialiser.valid(model);
	}

	private static String blameNonserialisability(IBakedModel model) {
		assert !canSerialise(model);

		@SuppressWarnings("unchecked") //They're added to the map by type so this should be fine
		ModelAdapter<IBakedModel> serialiser = (ModelAdapter<IBakedModel>) KNOWN_MODELS.get(model.getClass());
		if (serialiser == null) return "Unknown model type";

		Set<Class<? extends IBakedModel>> blamedTypes = serialiser.blameNonvalidity(model);
		return !blamedTypes.isEmpty() ? blamedTypes.stream().map(Class::getName).collect(Collectors.joining(", ", "Non-serialisable contained models: ", "")) : "Serialiser rejected";
	}

	public static void serialisablility(Map<?, IBakedModel> models, Map<ResourceLocation, IBakedModel> topModels) {
		Set<IBakedModel> seenModels = new ReferenceOpenHashSet<>(models.size());
		int totalValid = 0;
		Reference2IntMap<Class<? extends IBakedModel>> valid = Util.make(new Reference2IntOpenHashMap<>(), map -> map.defaultReturnValue(-1));
		int totalRejected = 0;
		Reference2IntMap<Class<? extends IBakedModel>> rejected = Util.make(new Reference2IntOpenHashMap<>(), map -> map.defaultReturnValue(-1));

		final boolean findCause = FoamyConfig.LOG_MODELS_BLAME.asBoolean();
		Map<Class<? extends IBakedModel>, Set<String>> blameMap = findCause ? new IdentityHashMap<>() : null;

		for (IBakedModel model : Iterables.concat(topModels.values(), models.values())) {//Loop top models first as mods can replace these via ModelBakeEvent
			if (model != null && seenModels.add(model)) {
				Reference2IntMap<Class<? extends IBakedModel>> map;
				if (canSerialise(model)) {
					totalValid++;
					map = valid;
				} else {
					if (findCause) {
						String cause = blameNonserialisability(model);
						if (blameMap.computeIfAbsent(model.getClass(), k -> new ObjectOpenHashSet<>()).add(cause)) {
							LOGGER.warn("Can't serialise {}: {}", model.getClass().getName(), cause);
						}
					}
					totalRejected++;
					map = rejected;
				}
				Class<? extends IBakedModel> key = model.getClass();

				int value = map.getInt(key);
				if (value < 0) {
					map.put(key, 1);
				} else {
					map.put(key, value + 1);
				}
			}
		}

		LOGGER.printf(Level.INFO, "Out of the %d models, %.1f%% were serialisable:", seenModels.size(), (totalValid * 100F) / seenModels.size());
		if (!valid.isEmpty()) {
			LOGGER.info("\tOf the {} serialisable models, there were {} types:", totalValid, valid.size());
			for (Reference2IntMap.Entry<Class<? extends IBakedModel>> entry : Reference2IntMaps.fastIterable(valid)) {
				LOGGER.info("\t\t{} were {}", entry.getIntValue(), entry.getKey().getName());
			}
		} else LOGGER.warn("No models were serialisable!");
		if (!rejected.isEmpty()) {
			LOGGER.info("\tOf the {} non-serialisable models, there were {} types:", totalRejected, rejected.size());
			for (Reference2IntMap.Entry<Class<? extends IBakedModel>> entry : Reference2IntMaps.fastIterable(rejected)) {
				LOGGER.info("\t\t{} were {}", entry.getIntValue(), entry.getKey().getName());
			}
		} else LOGGER.info("No models were non-serialisable");
	}

	public static Set<ResourceLocation> serialise(Map<?, IBakedModel> models, Map<ResourceLocation, IBakedModel> topModels, Path to) {
		Map<Object, IBakedModel> allModels = new Object2ReferenceLinkedOpenHashMap<>(models.size());
		Set<ResourceLocation> rejects = new ObjectOpenHashSet<>();

		assert reverseModelMap.isEmpty();
		for (Entry<ResourceLocation, IBakedModel> entry : Util.entrySet(topModels)) {//Loop top models first as mods can replace these via ModelBakeEvent
			Object key = FoamyConfig.THREAD_MODELS.asBoolean() ? new ModelKey(entry.getKey(), ModelRotation.X0_Y0.getRotation(), ModelRotation.X0_Y0.isUvLock())
																: Triple.of(entry.getKey(), ModelRotation.X0_Y0.getRotation(), ModelRotation.X0_Y0.isUvLock());

			IBakedModel model = entry.getValue();
			if (canSerialise(model)) {
				if (!reverseModelMap.containsKey(model)) {
					allModels.put(key, model);
					reverseModelMap.put(model, key);
				} else {
					allModels.put(key, new DelayedModel(reverseModelMap.get(model)));
				}
			} else {
				rejects.add(entry.getKey());
			}
		}
		for (Entry<?, IBakedModel> entry : Util.entrySet(models)) {
			if (!allModels.containsKey(entry.getKey())) {
				IBakedModel model = entry.getValue();

				if (canSerialise(model)) {
					if (!reverseModelMap.containsKey(model)) {
						allModels.put(entry.getKey(), model);
						reverseModelMap.put(model, entry.getKey());
					} else {
						allModels.put(entry.getKey(), new DelayedModel(reverseModelMap.get(model)));
					}
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

		LOGGER.debug("Wrote {} models (from {} + {}) skipping {}", allModels.size(), topModels.size(), models.size(), rejects.size());
		return rejects;
	}

	public static void serialise(Set<ResourceLocation> rejectedModels, Path to) {
		try (Writer out = Files.newBufferedWriter(to)) {
			GSON.toJson(rejectedModels, REJECTS_TYPE, out);
		} catch (IOException e) {
			throw new RuntimeException("Error writing " + rejectedModels.size() + " rejected models to " + to, e);
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
		Set<Object> seenKeys = new ObjectOpenHashSet<>(models.size());
		Map<Object, Object> swaps = new Object2ObjectOpenHashMap<>();

		final JsonPrimitive delayedType = new JsonPrimitive(DelayedModel.class.getName());
		for (Entry<?, JsonObject> entry : Util.entrySet(models)) {
			JsonObject model = entry.getValue();

			if (model.size() == 2 && delayedType.equals(model.getAsJsonPrimitive("type")) && model.has("actual")) {//DelayedModels are already as simple as they need to be
				Object key = GSON.fromJson(model.get("actual"), MAP_KEY_TYPE);

				Object newKey = swaps.get(key);
				if (newKey != null) {
					//LOGGER.debug("Swapping " + key + " to " + newKey);
					model.add("actual", GSON.toJsonTree(newKey, MAP_KEY_TYPE));
				} else if (!seenKeys.contains(key)) {
					LOGGER.error(entry.getKey() + " depends on " + key + " before it is defined!");
				}
			} else {
				seenKeys.add(entry.getKey());
				Object newKey = uniqueModels.get(model);//Have we already seen this (actual) model?

				if (newKey == null) {//No, note the model down
					uniqueModels.put(model, entry.getKey());
				} else {//Yes, reuse
					swaps.put(entry.getKey(), newKey);
					entry.setValue(GSON.toJsonTree(new DelayedModel(newKey), IBakedModel.class).getAsJsonObject());
				}
			}
		}

		LOGGER.debug("Of {} models at {} locations, {} are unnecessary duplicates", models.size(), seenKeys.size(), swaps.size());
		try (Writer out = Files.newBufferedWriter(from)) {
			GSON.toJson(models, mapType, out);
		} catch (IOException e) {
			throw new RuntimeException("Error writing " + uniqueModels.size() + " (from " + models.size() + ") to " + from, e);
		}
	}

	public static Map<?, IBakedModel> deserialise(Path from) {
		try (Reader in = Files.newBufferedReader(from)) {
			Map<?, IBakedModel> out = GSON.fromJson(in, MAP_TYPE);

			for (Entry<?, IBakedModel> entry : Util.entrySet(out)) {
				IBakedModel model = entry.getValue();

				if (model instanceof DelayedModel) {
					Object key = ((DelayedModel) model).real;
					model = out.get(key);

					if (model == null && !out.containsKey(key)) {
						LOGGER.warn("Lost reused model: " + key + '?');
						entry.setValue(null); //It's null anyway...
					} else if (model instanceof DelayedModel) {
						throw new IllegalStateException("Needed model which itself is needed: " + key);
					} else {
						entry.setValue(model);
					}
				}
			}

			for (AwaitingModel<?> field : fieldsToFix) {
				field.fill(out);
			}

			return out;
		} catch (IOException e) {
			throw new RuntimeException("Error reading models from " + from, e);
		} finally {
			for (AwaitingModel<?> field : fieldsToFix) {
				field.clear();
			}
		}
	}

	public static Set<ResourceLocation> deserialiseRejects(Path from) {
		try (Reader in = Files.newBufferedReader(from)) {
			return GSON.fromJson(in, REJECTS_TYPE);
		} catch (IOException e) {
			throw new RuntimeException("Error reading models from " + from, e);
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
			LOGGER.error("The model for " + model + " was unimpressed");
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
				LOGGER.error("The model for " + model + " was unimpressed", t);
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
			LOGGER.error("The model for " + model + " was unimpressed");
			return true;
		}

		return equal(expectedSprite, actualModel.getParticleTexture(EmptyModelData.INSTANCE));
	}

	public static void compare(Map<?, IBakedModel> models, Set<ResourceLocation> skipped) {
		Map<ResourceLocation, IBakedModel> expectedModels = ModelLoader.instance().getTopBakedModels();
		LOGGER.info("Validating " + expectedModels.size() + " expected models (from " + models.size() + ')');

		Random random = new Random();
		for (Block block : ForgeRegistries.BLOCKS) {
			for (BlockState state : block.getStateContainer().getValidStates()) {
				ModelResourceLocation model = BlockModelShapes.getModelLocation(state);
				if (skipped.contains(model)) continue;

				IBakedModel expectedModel = expectedModels.get(model);
				if (expectedModel != null) {
					Object key = FoamyConfig.THREAD_MODELS.asBoolean() ? new ModelKey(model, ModelRotation.X0_Y0.getRotation(), ModelRotation.X0_Y0.isUvLock()) : Triple.of(model, ModelRotation.X0_Y0.getRotation(), ModelRotation.X0_Y0.isUvLock());
					//Object key = FoamyConfig.THREAD_MODELS.asBoolean() ? new ModelKey(model, TransformationMatrix.identity().getMatrix(), ModelRotation.X0_Y0.isUvLock()) : Triple.of(model, ModelRotation.X0_Y0.getRotation(), ModelRotation.X0_Y0.isUvLock());
					IBakedModel actualModel = models.get(key);

					if (actualModel == null) {
						LOGGER.error("Lost the model for " + model);
						LOGGER.error("");
					} else if (!equal(random, state, model, expectedModel, actualModel)) {
						LOGGER.error("Produced different model for " + model);
						if (expectedModel.getClass() != actualModel.getClass()) LOGGER.error("Produced " + actualModel.getClass() + " rather than " + expectedModel.getClass());
						LOGGER.error("");
					}
				} else {
					LOGGER.warn("Apparently got null top model for " + model);
					LOGGER.warn("");
				}
			}
		}

		for (Item item : ForgeRegistries.ITEMS) {
			if (item != Items.AIR) {
				ModelResourceLocation model = new ModelResourceLocation(item.getRegistryName(), "inventory");
				if (skipped.contains(model)) continue;

				IBakedModel expectedModel = expectedModels.get(model);
				if (expectedModel != null) {
					Object key = FoamyConfig.THREAD_MODELS.asBoolean() ? new ModelKey(model, ModelRotation.X0_Y0.getRotation(), ModelRotation.X0_Y0.isUvLock()) :
																			Triple.of(model, ModelRotation.X0_Y0.getRotation(), ModelRotation.X0_Y0.isUvLock());
					IBakedModel actualModel = models.get(key);

					if (actualModel == null) {
						LOGGER.error("Lost the model for " + model);
						LOGGER.error("");
					} else if (!equal(random, null, model, expectedModel, actualModel)) {
						LOGGER.error("Produced different model for " + model);
						LOGGER.error("");
					}
				} else {
					LOGGER.warn("Apparently got null top model for " + model);
					LOGGER.warn("");
				}
			}
		}

		LOGGER.info("Check complete");
	}
}