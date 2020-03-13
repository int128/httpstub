package org.hidetake.stubyaml.service.rules;

import org.hidetake.stubyaml.model.yaml.RuleContainer;
import org.hidetake.stubyaml.service.YamlParser;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class CompositeRulesCompiler extends RulesV11Compiler {

    public CompositeRulesCompiler(YamlParser yamlParser) {
        super(yamlParser);
    }

    @Override
    public RuleContainer compile(File file) {
        return (RuleContainer) super.compile(file);
    }

}
