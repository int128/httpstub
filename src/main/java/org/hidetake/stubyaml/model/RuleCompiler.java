package org.hidetake.stubyaml.model;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.codehaus.groovy.control.CompilationFailedException;
import org.hidetake.stubyaml.model.execution.CompiledRule;
import org.hidetake.stubyaml.model.execution.CompiledTable;
import org.hidetake.stubyaml.model.yaml.Rule;
import org.hidetake.stubyaml.model.yaml.Table;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
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
            .tables(compileTables(response.getTables()))
            .build();
    }

    private List<CompiledTable> compileTables(List<Table> tables) {
        return tables.stream()
            .map(table -> {
                if (!StringUtils.hasText(table.getName())) {
                    log.error("Table name is null, ignored {}", table);
                    return null;
                }
                if (!StringUtils.hasText(table.getKey())) {
                    log.error("Table key is null, ignored {}", table);
                    return null;
                }
                if (ObjectUtils.isEmpty(table.getValues())) {
                    log.error("Table values are empty, ignored {}", table);
                    return null;
                }
                return CompiledTable.builder()
                    .name(table.getName())
                    .expression(expressionCompiler.compile(table.getKey()))
                    .values(table.getValues())
                    .build();
            })
            .filter(Objects::nonNull)
            .collect(toList());
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
