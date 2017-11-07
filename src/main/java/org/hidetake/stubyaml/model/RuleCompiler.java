package org.hidetake.stubyaml.model;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.hidetake.stubyaml.model.execution.CompiledRule;
import org.hidetake.stubyaml.model.yaml.Rule;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.springframework.util.Assert.notNull;

@RequiredArgsConstructor
@Component
public class RuleCompiler {
    private final ExpressionCompiler expressionCompiler;
    private final TemplateCompiler templateCompiler;
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
                .collect(toMap(Map.Entry::getKey, entry -> templateCompiler.compile(entry.getValue()))))
            .body(templateCompiler.compile(response.getBody()))
            .tables(response.getTables()
                .stream()
                .map(tableCompiler::compile)
                .filter(Objects::nonNull)
                .collect(toList()))
            .build();
    }
}
