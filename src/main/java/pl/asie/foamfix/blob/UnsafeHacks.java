package pl.asie.foamfix.blob;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

import net.minecraft.util.Util;

@SuppressWarnings("restriction")
public class UnsafeHacks extends net.minecraftforge.fml.unsafe.UnsafeHacks {
	private static final Unsafe UNSAFE = Util.make(() -> {
		try {
			for (Field field : UnsafeHacks.class.getSuperclass().getDeclaredFields()) {
				if (field.getType() == Unsafe.class) {
					field.setAccessible(true);
					return (Unsafe) field.get(null);
				}
			}

			throw new NoSuchFieldException(); //Mysterious
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Unable to grab completely safe field", e);
		}
	});

	public static void setBooleanField(Field data, Object object, boolean value) {
		long offset = UNSAFE.objectFieldOffset(data);
		UNSAFE.putBoolean(object, offset, value);
	}

	public static void setByteField(Field data, Object object, byte value) {
		long offset = UNSAFE.objectFieldOffset(data);
		UNSAFE.putByte(object, offset, value);
	}

	public static void setCharField(Field data, Object object, char value) {
		long offset = UNSAFE.objectFieldOffset(data);
		UNSAFE.putChar(object, offset, value);
	}

	public static void setShortField(Field data, Object object, short value) {
		long offset = UNSAFE.objectFieldOffset(data);
		UNSAFE.putShort(object, offset, value);
	}

	public static void setLongField(Field data, Object object, long value) {
		long offset = UNSAFE.objectFieldOffset(data);
		UNSAFE.putLong(object, offset, value);
	}

	public static void setFloatField(Field data, Object object, float value) {
		long offset = UNSAFE.objectFieldOffset(data);
		UNSAFE.putFloat(object, offset, value);
	}

	public static void setDoubleField(Field data, Object object, double value) {
		long offset = UNSAFE.objectFieldOffset(data);
		UNSAFE.putDouble(object, offset, value);
	}
}