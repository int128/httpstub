package org.hidetake.stubyaml.model.execution;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class RequestContext {
    private final Map<String, String> requestHeaders;
    private final Map<String, String> pathVariables;
    private final Map<String, String> requestParams;
    private final Object requestBody;
}
