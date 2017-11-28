package org.hidetake.stubyaml.model.execution;

import lombok.Builder;
import lombok.Data;
import lombok.val;

@Data
@Builder
public class CompiledRule {
    private final CompiledExpression when;
    private final CompiledResponse response;

    public boolean matches(RequestContextMap requestContextMap) {
        if (when == null) {
            return true;
        } else {
            val result = when.evaluate(requestContextMap);
            return (result instanceof Boolean) && (Boolean) result;
        }
    }
}
