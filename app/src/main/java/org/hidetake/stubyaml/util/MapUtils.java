package org.hidetake.stubyaml.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MapUtils {

    public static <K, V1, V2> Map<K, V2> mapValue(Map<K, V1> source, Function<V1, V2> transform) {
        final var target = new HashMap<K, V2>(source.size());
        source.forEach((key, value) -> target.put(key, transform.apply(value)));

        return target;
    }

    public static <K, V1, V2> MultiValueMap<K, V2> mapMultiValue(MultiValueMap<K, V1> source, Function<V1, V2> transform) {
        final var target = new LinkedMultiValueMap<K, V2>(source.size());
        source.forEach((key, value) -> target.put(key, value.stream().map(transform).collect(Collectors.toList())));
        return target;
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.size() == 0;
    }

}
