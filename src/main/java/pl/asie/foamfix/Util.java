package pl.asie.foamfix;

import java.util.Map;
import java.util.function.Function;

public class Util {
	public static <K, V> V syncIfAbsent(Map<K, V> map, K key, Function<? super K, ? extends V> ifAbsent) {
		synchronized (map) {
			return map.computeIfAbsent(key, ifAbsent);
		}
	}
}