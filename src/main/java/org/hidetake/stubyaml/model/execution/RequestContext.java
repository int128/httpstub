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
        val requestContext = new RequestContext(new HashMap<>(512));
        requestContext.binding.put("request", request);
        requestContext.binding.put("path", pathVariables);
        requestContext.binding.put("params", requestParams);
        requestContext.binding.put("body", requestBody);
        return requestContext;
    }
}
