package org.hidetake.stubyaml.model;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import groovy.text.SimpleTemplateEngine;
import groovy.text.TemplateEngine;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.codehaus.groovy.control.CompilationFailedException;
import org.hidetake.stubyaml.model.execution.CompiledRule;
import org.hidetake.stubyaml.model.execution.Template;
import org.hidetake.stubyaml.model.yaml.Rule;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.springframework.util.Assert.notNull;

@Slf4j
@Component
public class RuleCompiler {
    private final GroovyShell groovyShell = new GroovyShell();
    private final TemplateEngine templateEngine = new SimpleTemplateEngine(groovyShell);

    public CompiledRule compile(Rule rule) {
        notNull(rule, "rule should not be null");

        val response = rule.getResponse();
        notNull(response, "response should not be null");
        notNull(response.getHeaders(), "headers should not be null");

        return CompiledRule.builder()
            .when(toScript(rule.getWhen()))
            .status(response.getStatus())
            .headers(response.getHeaders()
                .entrySet()
                .stream()
                .collect(toMap(Map.Entry::getKey, entry -> toTemplate(entry.getValue()))))
            .body(toTemplate(response.getBody()))
            .build();
    }

    private Script toScript(String expression) {
        if (expression == null) {
            return null;
        }
        try {
            return groovyShell.parse(expression);
        } catch (CompilationFailedException e) {
            log.warn("Invalid expression {}", expression, e);
            return null;
        }
    }

    private Template toTemplate(String expression) {
        if (expression == null) {
            return null;
        }
        try {
            return new Template(templateEngine.createTemplate(expression));
        } catch (CompilationFailedException | ClassNotFoundException | IOException e) {
            log.warn("Invalid expression {}", expression, e);
            return null;
        }
    }
}
