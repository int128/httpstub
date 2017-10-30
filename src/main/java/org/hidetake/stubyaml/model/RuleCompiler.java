package org.hidetake.stubyaml.model;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.codehaus.groovy.control.CompilationFailedException;
import org.hidetake.stubyaml.model.execution.CompiledRule;
import org.hidetake.stubyaml.model.execution.Expression;
import org.hidetake.stubyaml.model.yaml.Rule;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.springframework.util.Assert.notNull;

@Slf4j
@Component
public class RuleCompiler {
    public CompiledRule compile(Rule rule) {
        notNull(rule, "rule should not be null");

        val response = rule.getResponse();
        notNull(response, "response should not be null");
        notNull(response.getHeaders(), "headers should not be null");

        return CompiledRule.builder()
            .when(toExpression(rule.getWhen()))
            .status(response.getStatus())
            .headers(response.getHeaders()
                .entrySet()
                .stream()
                .collect(toMap(Map.Entry::getKey, entry -> toTemplate(entry.getValue()))))
            .body(toTemplate(response.getBody()))
            .build();
    }

    private Expression toExpression(String expression) {
        if (expression == null) {
            return null;
        }
        try {
            return Expression.compile(expression);
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
            return new SimpleTemplateEngine().createTemplate(expression);
        } catch (CompilationFailedException | ClassNotFoundException | IOException e) {
            log.warn("Invalid expression {}", expression, e);
            return null;
        }
    }
}
