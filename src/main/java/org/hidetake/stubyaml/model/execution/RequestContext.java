package org.hidetake.stubyaml.model.execution;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.val;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class RequestContext {
    private final HttpServletRequest request;
    private final Map<String, String> requestHeaders;
    private final Map<String, String> pathVariables;
    private final Map<String, String> requestParams;
    private final Object requestBody;

    @Getter(lazy = true)
    private final Map<String, Object> binding = computeBinding();
    private Map<String, Object> computeBinding() {
        val binding = new HashMap<String, Object>();
        binding.put("request", request);
        binding.put("headers", requestHeaders);
        binding.put("path", pathVariables);
        binding.put("params", requestParams);
        binding.put("body", requestBody);
        return binding;
    }
}
