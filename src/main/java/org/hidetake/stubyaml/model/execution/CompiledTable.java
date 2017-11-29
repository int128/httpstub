package org.hidetake.stubyaml.model.execution;

import lombok.Builder;
import lombok.Data;
import lombok.val;
import org.springframework.util.ObjectUtils;

import java.util.Map;

@Data
@Builder
public class CompiledTable {
    private final String name;
    private final CompiledExpression keyExpression;
    private final Map<String, Object> values;

    public Object find(RequestContextMap requestContextMap) {
        val key = ObjectUtils.nullSafeToString(keyExpression.evaluate(requestContextMap));
        return values.get(key);
    }
}
