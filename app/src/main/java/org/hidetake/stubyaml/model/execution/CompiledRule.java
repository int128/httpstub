package org.hidetake.stubyaml.model.execution;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompiledRule {
    private final CompiledExpression when;
    private final CompiledResponse response;

    public boolean matches(RequestContext requestContext) {
        if (when == null) {
            return true;
        } else {
            final var result = when.evaluate(requestContext);
            return (result instanceof Boolean) && (Boolean) result;
        }
    }
}
