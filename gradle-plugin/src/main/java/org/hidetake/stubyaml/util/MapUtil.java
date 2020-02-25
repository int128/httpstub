package org.hidetake.stubyaml.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MapUtil {

    public static <K, V> Map<K, V> of(
        K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
        Map<K, V> map = newHashMap();

        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);

        return map;
    }

}
