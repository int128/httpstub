package org.hidetake.stubyaml.service.rules;

import lombok.RequiredArgsConstructor;
import org.hidetake.stubyaml.model.yaml.RouteSource;
import org.hidetake.stubyaml.model.yaml.Rule;
import org.hidetake.stubyaml.service.YamlParser;

@RequiredArgsConstructor
public abstract class RulesV10Compiler implements VersionCompiler {

    protected final YamlParser yamlParser;

    @Override
    public Object compile(RouteSource routeSource) {
        Rule[] rules = yamlParser.parse(routeSource.getFile(), Rule[].class);
        return rules;
    }

}
