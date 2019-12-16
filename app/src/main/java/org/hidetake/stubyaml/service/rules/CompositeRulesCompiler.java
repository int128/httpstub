package org.hidetake.stubyaml.service.rules;

import org.hidetake.stubyaml.model.yaml.RouteSource;
import org.hidetake.stubyaml.model.yaml.RuleContainer;
import org.hidetake.stubyaml.service.YamlParser;
import org.springframework.stereotype.Component;

@Component
public class CompositeRulesCompiler extends RulesV11Compiler {

    public CompositeRulesCompiler(YamlParser yamlParser) {
        super(yamlParser);
    }

    @Override
    public RuleContainer compile(RouteSource routeSource) {
        return (RuleContainer) super.compile(routeSource);
    }

}
