package org.hidetake.stubyaml.model.execution;

import groovy.lang.Binding;
import groovy.lang.Script;
import lombok.Data;
import lombok.val;

@Data
public class CompiledExpression {
    private final Class<Script> clazz;

    public Object evaluate(ContextMap contextMap) {
        try {
            val script = clazz.newInstance();
            script.setBinding(new Binding(contextMap.getBinding()));
            return script.run();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
