package org.hidetake.stubyaml.model.execution;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import lombok.Data;
import lombok.val;
import org.codehaus.groovy.control.CompilationFailedException;

import java.util.Map;

@Data
public class Expression {
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

    @SuppressWarnings("unchecked")
    public static Expression compile(String expressionString) throws CompilationFailedException {
        val clazz = new GroovyClassLoader().parseClass(expressionString);
        return new Expression((Class<Script>) clazz);
    }
}
