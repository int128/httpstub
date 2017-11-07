package org.hidetake.stubyaml.model;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.codehaus.groovy.control.CompilationFailedException;
import org.hidetake.stubyaml.model.execution.CompiledRule;
import org.hidetake.stubyaml.model.yaml.Rule;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.springframework.util.Assert.notNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class RuleCompiler {
    private final ExpressionCompiler expressionCompiler;
    private final TableCompiler tableCompiler;

    public CompiledRule compile(Rule rule) {
        notNull(rule, "rule should not be null");

        val response = rule.getResponse();
        notNull(response, "response should not be null");
        notNull(response.getHeaders(), "headers should not be null");

        return CompiledRule.builder()
            .when(expressionCompiler.compile(rule.getWhen()))
            .status(response.getStatus())
            .headers(response.getHeaders()
                .entrySet()
                .stream()
                .collect(toMap(Map.Entry::getKey, entry -> toTemplate(entry.getValue()))))
            .body(toTemplate(response.getBody()))
            .tables(response.getTables()
                .stream()
                .map(tableCompiler::compile)
                .filter(Objects::nonNull)
                .collect(toList()))
            .build();
    }

    private static Template toTemplate(String expression) {
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
