package pl.asie.foamfix;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMaps;

public class Util extends net.minecraft.util.Util {
	public static <K, V> V syncIfAbsent(Map<K, V> map, K key, Function<? super K, ? extends V> ifAbsent) {
		synchronized (map) {
			return map.computeIfAbsent(key, ifAbsent);
		}
	}

	public static <K, V> Iterable<? extends Entry<K, V>> entrySet(Map<K, V> map) {
		if (map instanceof Object2ObjectMap) {
			return Object2ObjectMaps.fastIterable((Object2ObjectMap<K, V>) map);
		} else if (map instanceof Object2ReferenceMap) {
			return Object2ReferenceMaps.fastIterable((Object2ReferenceMap<K, V>) map);
		}

		return map.entrySet();
	}
}