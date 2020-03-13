package org.hidetake.stubyaml.service;

import lombok.RequiredArgsConstructor;
import org.hidetake.stubyaml.model.RuleCompiler;
import org.hidetake.stubyaml.model.execution.CompiledRule;
import org.hidetake.stubyaml.model.yaml.RouteSource;
import org.hidetake.stubyaml.model.yaml.RuleContainer;
import org.hidetake.stubyaml.service.rules.CompositeRulesCompiler;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class RulesCompiler {

    private final CompositeRulesCompiler compositeRulesCompiler;
    private final RuleCompiler ruleCompiler;

    public List<CompiledRule> compile(RouteSource routeSource) {
        RuleContainer ruleContainer = compositeRulesCompiler.compile(routeSource.getFile());
        List<CompiledRule> output = ruleContainer.getRules().stream()
            .map(rule -> ruleCompiler.compile(routeSource, rule))
            .collect(toList());

        return output;
    }

}
