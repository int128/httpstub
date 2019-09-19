package org.hidetake.stubyaml.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MapUtils {
    public static <K, V1, V2> Map<K, V2> mapValue(Map<K, V1> source, Function<V1, V2> transform) {
        final var target = new HashMap<K, V2>(source.size());
        source.forEach((key, value) -> target.put(key, transform.apply(value)));
        return target;
    }
}
