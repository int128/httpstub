package org.hidetake.stubyaml.model.execution;

import groovy.lang.Script;
import lombok.Data;

import java.lang.reflect.InvocationTargetException;

@Data
public class CompiledExpression {
    // a class to evaluate the script, compiled by Groovy
    private final Class<Script> clazz;

    public Object evaluate(Bindable bindable) {
        try {
            final var script = clazz.getConstructor().newInstance();
            script.setBinding(bindable.getBinding());
            return script.run();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
