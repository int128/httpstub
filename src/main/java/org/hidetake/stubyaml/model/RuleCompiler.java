package org.hidetake.stubyaml.model;

import lombok.RequiredArgsConstructor;
import org.hidetake.stubyaml.model.execution.CompiledRule;
import org.hidetake.stubyaml.model.yaml.Rule;
import org.springframework.stereotype.Component;

import static org.springframework.util.Assert.notNull;

@RequiredArgsConstructor
@Component
public class RuleCompiler {
    private final ExpressionCompiler expressionCompiler;
    private final ResponseCompiler responseCompiler;

    public CompiledRule compile(Rule rule) {
        notNull(rule, "rule should not be null");
        return CompiledRule.builder()
            .when(expressionCompiler.compileExpression(rule.getWhen()))
            .response(responseCompiler.compile(rule.getResponse()))
            .build();
    }
}
