package org.hidetake.stubyaml.model;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.hidetake.stubyaml.model.execution.CompiledRule;
import org.hidetake.stubyaml.model.yaml.Rule;
import org.springframework.stereotype.Component;

import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.ObjectUtils.nullSafeToString;

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
            .body(templateCompiler.compile(nullSafeToString(response.getBody())))
            .tables(tableCompiler.compile(response.getTables()))
            .build();
    }
}
