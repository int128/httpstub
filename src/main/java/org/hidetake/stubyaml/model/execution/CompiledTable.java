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
    private final Expression expression;
    private final Map<String, String> values;

    public String lookup(RequestContext requestContext) {
        val evaluatedKey = ObjectUtils.nullSafeToString(expression.evaluate(requestContext.getBinding()));
        return values.get(evaluatedKey);
    }
}
