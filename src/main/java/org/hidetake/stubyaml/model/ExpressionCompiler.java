package org.hidetake.stubyaml.model;

import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.codehaus.groovy.control.CompilationFailedException;
import org.hidetake.stubyaml.model.execution.CompiledExpression;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExpressionCompiler {
    @SuppressWarnings("unchecked")
    public CompiledExpression compile(String expression) throws CompilationFailedException {
        if (expression == null) {
            return null;
        }
        try {
            val clazz = new GroovyClassLoader().parseClass(expression);
            return new CompiledExpression((Class<Script>) clazz);
        } catch (CompilationFailedException e) {
            log.warn("Invalid expression {}", expression, e);
            return null;
        }
    }
}
