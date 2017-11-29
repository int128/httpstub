package org.hidetake.stubyaml.model.execution;

import lombok.Data;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

@Data
public class ResponseContextMap implements ContextMap {
    private final Map<String, Object> binding;

    public static ResponseContextMap of(ResponseContext responseContext) {
        val binding = new HashMap<String, Object>();
        binding.putAll(RequestContextMap.of(responseContext.getRequestContext()).getBinding());
        binding.put("table", responseContext.getResolvedTable().getMap());
        return new ResponseContextMap(binding);
    }
}
