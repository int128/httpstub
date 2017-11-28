package org.hidetake.stubyaml.model.execution;

import lombok.Data;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

@Data
public class RequestContextMap implements ContextMap {
    private final Map<String, Object> binding;

    public static RequestContextMap of(RequestContext requestContext) {
        val binding = new HashMap<String, Object>();
        binding.put("headers", requestContext.getRequestHeaders());
        binding.put("path", requestContext.getPathVariables());
        binding.put("params", requestContext.getRequestParams());
        binding.put("body", requestContext.getRequestBody());
        return new RequestContextMap(binding);
    }
}
