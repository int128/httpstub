package org.hidetake.stubyaml.model.execution;

import lombok.Data;
import lombok.val;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Data
public class RequestContext {
    private final Map<String, Object> binding;

    public static RequestContext create(
        HttpServletRequest request,
        Map<String, String> pathVariables,
        Map<String, String> requestParams,
        Object requestBody
    ) {
        val binding = new HashMap<String, Object>(512);
        binding.put("request", request);
        binding.put("path", pathVariables);
        binding.put("params", requestParams);
        binding.put("body", requestBody);
        return new RequestContext(binding);
    }
}
