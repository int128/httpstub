package org.hidetake.stubyaml.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Data
@Builder
public class RequestContext {
    private final Map<String, String> pathVariables;
    private final Map<String, String> requestParams;
    private final Map<String, String> requestBody;

    /**
     * Find value from path variables, request parameters and request body in order.
     *
     * @param key key
     * @return value or {@code null}
     */
    public String find(String key) {
        return Stream.of(
                pathVariables.get(key),
                requestParams.get(key),
                requestBody != null ? requestBody.get(key) : null
            )
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }
}
