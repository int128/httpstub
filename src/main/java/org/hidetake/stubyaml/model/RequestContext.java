package org.hidetake.stubyaml.model;

import lombok.Data;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

@Data
public class RequestContext {
    private final Map<String, Object> binding;

    public static RequestContext create(
        Map<String, String> pathVariables,
        Map<String, String> requestParams,
        Map<String, Object> requestBody
    ) {
        val requestContext = new RequestContext(new HashMap<>(512));
        requestContext.binding.put("path", pathVariables);
        requestContext.binding.put("params", requestParams);
        requestContext.binding.put("body", requestBody);
        return requestContext;
    }
}
