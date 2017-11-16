package org.hidetake.stubyaml.model.execution;

import groovy.lang.Binding;
import groovy.lang.Script;
import lombok.Data;
import lombok.val;

import java.util.Map;

@Data
public class CompiledExpression {
    private final Class<Script> clazz;

    public Object evaluate(Map binding) {
        try {
            val script = clazz.newInstance();
            script.setBinding(new Binding(binding));
            return script.run();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
