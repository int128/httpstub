package org.hidetake.stubyaml.service.rules;

import lombok.RequiredArgsConstructor;
import org.hidetake.stubyaml.model.yaml.Rule;
import org.hidetake.stubyaml.service.YamlParser;

import java.io.File;

@RequiredArgsConstructor
public abstract class RulesV10Compiler implements VersionCompiler {

    protected final YamlParser yamlParser;

    @Override
    public Object compile(File file) {
        return yamlParser.parse(file, Rule[].class);
    }

}
