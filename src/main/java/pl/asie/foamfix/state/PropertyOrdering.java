package pl.asie.foamfix.state;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntMaps;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;

import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.util.math.MathHelper;

import pl.asie.foamfix.Util;

public class PropertyOrdering {
	public static abstract class Entry {
		final Property<?> property;
		final int bitSize;
		final int bits;

		private Entry(Property<?> property) {
			this.property = property;
			this.bitSize = MathHelper.smallestEncompassingPowerOfTwo(property.getAllowedValues().size());

			int bits = 0;
			for (int b = bitSize - 1; b != 0; b >>= 1) {
				bits++;
			}
			this.bits = bits;
		}

		public abstract int get(Object v);

		@Override
		public boolean equals(Object other) {
			if (!(other instanceof Entry))
				return false;

			return ((Entry) other).property.equals(property);
		}

		@Override
		public int hashCode() {
			return property.hashCode();
		}
	}

	public static class BooleanEntry extends Entry {
		private BooleanEntry(Property<?> property) {
			super(property);
		}

		@Override
		public int get(Object v) {
			return v == Boolean.TRUE ? 1 : v == Boolean.FALSE ? 0 : -1;
		}
	}

	public static class ObjectEntry extends Entry {
		private final Object2IntMap<Object> values;

		private ObjectEntry(Property<?> property) {
			super(property);

			Object2IntOpenHashMap<Object> values = new Object2IntOpenHashMap<>();
			values.defaultReturnValue(-1);

			Collection<?> allowedValues = property.getAllowedValues();
			int i = 0;
			for (Object o : allowedValues) {
				values.put(o, i++);
			}

			values.trim();
			this.values = Object2IntMaps.unmodifiable(values);
		}

		@Override
		public int get(Object v) {
			return values.getInt(v);
		}
	}

	public static class EnumEntrySorted extends Entry {
		private EnumEntrySorted(Property<?> property, int count) {
			super(property);
		}

		@Override
		public int get(Object v) {
			return v instanceof Enum && ((Enum<?>) v).getDeclaringClass() == property.getValueClass() ? ((Enum<?>) v).ordinal() : -1;
		}

		public static Entry create(EnumProperty<?> property) {
			Object[] values = property.getValueClass().getEnumConstants();

			if (property.getAllowedValues().size() == values.length) {
				return new EnumEntrySorted(property, values.length);
			}

			Enum<?>[] sorted = property.getAllowedValues().stream().sorted().toArray(Enum[]::new);
			if (sorted[sorted.length - 1].ordinal() - sorted[0].ordinal() == sorted.length + 1) {
				return new IntegerEntrySorted(property, sorted[0].ordinal(), sorted.length) {
					@Override
					public int get(Object v) {
						return v instanceof Enum && ((Enum<?>) v).getDeclaringClass() == property.getValueClass() ? map(((Enum<?>) v).ordinal()) : -1;
					}
				};
			}

			Int2IntOpenHashMap map = new Int2IntOpenHashMap();
			map.defaultReturnValue(-1);

			int i = 0;
			for (Enum<?> entry : sorted) {
				if (map.put(entry.ordinal(), i++) != -1) {
					throw new IllegalStateException("EnumProperty has duplicated elements: " + entry + ", " + Arrays.toString(sorted));
				};
			}

			map.trim();
			return new IntegerEntry(property, map) {
				@Override
				public int get(Object v) {
					return v instanceof Enum && ((Enum<?>) v).getDeclaringClass() == property.getValueClass() ? map(((Enum<?>) v).ordinal()) : -1;
				}
			};
		}
	}

	public static class IntegerEntrySorted extends Entry {
		private final int minValue, count;

		IntegerEntrySorted(Property<?> property, int minValue, int count) {
			super(property);

			this.minValue = minValue;
			this.count = count;
		}

		@Override
		public int get(Object v) {
			return v instanceof Integer ? map((Integer) v) : -1;
		}

		protected final int map(int value) {
			int vv = value - minValue;
			// if vv < 0, it will be rejected anyway
			return vv < count ? vv : -1;
		}
	}

	public static class IntegerEntry extends Entry {
		private final Int2IntMap values;

		private IntegerEntry(Property<?> property) {
			super(property);

			Int2IntOpenHashMap values = new Int2IntOpenHashMap();
			values.defaultReturnValue(-1);
			
			Collection<?> allowedValues = property.getAllowedValues();
			int i = 0;
			for (Object o : allowedValues) {
				if (values.put((int) o, i++) != -1) {
					throw new IllegalStateException("IntegerProperty has duplicated elements: " + property + ", " + allowedValues);
				};
			}

			values.trim();
			this.values = Int2IntMaps.unmodifiable(values);
		}

		IntegerEntry(Property<?> property, Int2IntMap values) {
			super(property);

			this.values = Int2IntMaps.unmodifiable(values);
		}

		@Override
		public int get(Object v) {
			return v instanceof Integer ? map((Integer) v) : -1;
		}

		protected final int map(int value) {
			return values.get(value);
		}

		public static Entry create(IntegerProperty entry) {
			int[] sorted = IntIterators.unwrap(IntIterators.asIntIterator(entry.getAllowedValues().iterator()));
			Arrays.sort(sorted);

			int min = sorted[0];
			if (sorted[sorted.length - 1] - min == sorted.length + 1) {
				return new IntegerEntrySorted(entry, min, sorted.length);
			}

			return new IntegerEntry(entry);
		}
	}

	private PropertyOrdering() {

	}

	private static final Map<Property<?>, Entry> entryMap = new Object2ReferenceOpenHashMap<>();

	static Entry getEntry(Property<?> property) {
		Entry e = entryMap.get(property);
		if (e == null) {
			e = Util.syncIfAbsent(entryMap, property, key -> {
				if (key instanceof IntegerProperty) {
					return IntegerEntry.create((IntegerProperty) key);
				} else if (key.getClass() == BooleanProperty.class && key.getAllowedValues().size() == 2) {
					return new BooleanEntry(key);
				} else if (key instanceof EnumProperty) {
					return EnumEntrySorted.create((EnumProperty<?>) key);
				} else {
					return new ObjectEntry(key);
				}
			});
		}
		return e;
	}
}
