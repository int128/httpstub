package org.hidetake.stubyaml.model.execution;

import groovy.lang.Binding;
import groovy.lang.Script;
import lombok.Data;
import lombok.SneakyThrows;

import java.lang.reflect.InvocationTargetException;

@Data
public class CompiledExpression {
    // a class to evaluate the script, compiled by Groovy
    private final Class<Script> clazz;

    @SneakyThrows
    public Object evaluate(Bindable bindable) {
        Object output = null;
        try {
            final var script = clazz.newInstance();
            Binding binding = new Binding(bindable.getBinding());
            script.setBinding(binding);
            output = script.run();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        return output;
    }

}
