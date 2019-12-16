package org.hidetake.stubyaml.model;

import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import org.hidetake.stubyaml.model.execution.CompiledExpression;
import org.hidetake.stubyaml.service.ObjectCompiler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ExpressionCompiler implements ObjectCompiler {

    public final GroovyClassLoader GROOVY_CLASS_LOADER = new GroovyClassLoader();
    public static final String MULTI_STRINGS = "\"\"\"";

    /**
     * Compile a Groovy expression.
     * For example, {@code 1 + 1} will be {@code 2} on evaluated.
     *
     * @param expression Groovy expression
     * @return compiled
     */
    @SuppressWarnings("unchecked")
    public CompiledExpression compileExpression(String expression) {
        if (expression == null) {
            return null;
        }

        final var clazz = GROOVY_CLASS_LOADER.parseClass(expression);
        return new CompiledExpression((Class<Script>) clazz);
    }

    /**
     * Compile a Groovy template.
     * If it begins with <code>${</code> and ends with <code>}</code>, it will be treated as an expression.
     * For example, {@code user${id}} will be {@code used100} if {@code id} is {@code 100}.
     *
     * @param template Groovy template
     * @return compiled
     */
    public CompiledExpression compileTemplate(String template) {
        if (template == null) return null;
        String localTemplate = template.trim();
        if (localTemplate.startsWith("${") && localTemplate.endsWith("}")) {
            return compileExpression(localTemplate.substring(2, localTemplate.length() - 1));
        }

        return compileExpression(String.format("%s%s%s",
            MULTI_STRINGS,
            StringUtils.replace(localTemplate, MULTI_STRINGS, "\\\"\\\"\\\""),
            MULTI_STRINGS
        ));
    }

}
