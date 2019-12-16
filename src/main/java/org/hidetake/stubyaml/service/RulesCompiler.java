package org.hidetake.stubyaml.service;

import lombok.RequiredArgsConstructor;
import org.hidetake.stubyaml.model.RuleCompiler;
import org.hidetake.stubyaml.model.exception.StubyamlException;
import org.hidetake.stubyaml.model.execution.CompiledRule;
import org.hidetake.stubyaml.model.yaml.RouteSource;
import org.hidetake.stubyaml.model.yaml.Rule;
import org.hidetake.stubyaml.model.yaml.RuleContainer;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.constructor.ConstructorException;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class RulesCompiler {

    private final YamlParser yamlParser;
    private final RuleCompiler ruleCompiler;

    public List<CompiledRule> compile(RouteSource routeSource) {
        RuleContainer ruleContainer = parse(routeSource);
        List<CompiledRule> output;

        switch (ruleContainer.version()) {
            case V10:
            case V11:
                output = ruleContainer.getRules().stream()
                    .map(rule -> ruleCompiler.compile(routeSource, rule))
                    .collect(toList());
                break;
            default:
                throw StubyamlException.of("Unknown type of version %s", ruleContainer.version());
        }

        return output;
    }

    private RuleContainer parse(RouteSource routeSource) {
        RuleContainer container;
        try {
            container = yamlParser.parse(routeSource.getFile(), RuleContainer.class);
        } catch (ConstructorException e) {
            Rule[] rules = yamlParser.parse(routeSource.getFile(), Rule[].class);
            container = RuleContainer.backport(Arrays.asList(rules));
        }

        return container;
    }

}
