package org.hidetake.stubyaml.model;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.Script;
import lombok.val;
import org.hidetake.stubyaml.model.execution.CompiledExpression;
import org.hidetake.stubyaml.model.yaml.RouteSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ExpressionCompiler {
    /**
     * Compile a Groovy expression.
     * For example, {@code 1 + 1} will be {@code 2} on evaluated.
     * @param expression Groovy expression
     * @return compiled
     */
    @SuppressWarnings("unchecked")
    public CompiledExpression compileExpression(String expression, RouteSource source) {
        if (expression == null) {
            return null;
        }
        try {
            val clazz = new GroovyClassLoader().parseClass(expression, source.getName());
            return new CompiledExpression((Class<Script>) clazz);
        } catch (GroovyRuntimeException e) {
            throw new IllegalRuleException("Invalid expression: " + expression, source, e);
        }
    }

    /**
     * Compile a Groovy template.
     * If it begins with <code>${</code> and ends with <code>}</code>, it will be treated as an expression.
     * For example, {@code user${id}} will be {@code used100} if {@code id} is {@code 100}.
     * @param template Groovy template
     * @return compiled
     */
    public CompiledExpression compileTemplate(String template, RouteSource source) {
        if (template == null) {
            return null;
        }
        if (template.startsWith("${") && template.endsWith("}")) {
            return compileExpression(template.substring(2, template.length() - 1), source);
        }
        return compileExpression("\"\"\"" +
            StringUtils.replace(template, "\"\"\"", "\\\"\\\"\\\"") +
            "\"\"\"", source);
    }
}
