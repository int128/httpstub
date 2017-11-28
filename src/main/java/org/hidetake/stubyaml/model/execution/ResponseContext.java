package org.hidetake.stubyaml.model.execution;

import lombok.Data;

@Data
public class ResponseContext {
    private final RequestContext requestContext;
    private final ResolvedTable resolvedTable;

    public static ResponseContext of(CompiledResponse compiledResponse, RequestContext requestContext) {
        return new ResponseContext(requestContext,
            compiledResponse.getTables().resolve(
                RequestContextMap.of(requestContext)));
    }
}
