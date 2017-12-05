package org.hidetake.stubyaml.model;

import lombok.RequiredArgsConstructor;
import org.hidetake.stubyaml.model.execution.CompiledRule;
import org.hidetake.stubyaml.model.yaml.RouteSource;
import org.hidetake.stubyaml.model.yaml.Rule;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RuleCompiler {
    private final ExpressionCompiler expressionCompiler;
    private final ResponseCompiler responseCompiler;

    public CompiledRule compile(RouteSource source, Rule rule) {
        return CompiledRule.builder()
            .when(expressionCompiler.compileExpression(rule.getWhen(), source))
            .response(responseCompiler.compile(rule.getResponse(), source))
            .build();
    }
}
