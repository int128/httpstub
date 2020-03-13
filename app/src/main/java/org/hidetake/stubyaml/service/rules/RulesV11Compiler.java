package org.hidetake.stubyaml.service.rules;

import lombok.extern.slf4j.Slf4j;
import org.hidetake.stubyaml.model.exception.StubyamlException;
import org.hidetake.stubyaml.model.yaml.Rule;
import org.hidetake.stubyaml.model.yaml.RuleContainer;
import org.hidetake.stubyaml.model.yaml.Version;
import org.hidetake.stubyaml.service.YamlParser;
import org.yaml.snakeyaml.constructor.ConstructorException;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

@Slf4j
public abstract class RulesV11Compiler extends RulesV10Compiler {

    public RulesV11Compiler(YamlParser yamlParser) {
        super(yamlParser);
    }

    @Override
    public Object compile(File file) {
        RuleContainer container;
        try {
            container = yamlParser.parse(file, RuleContainer.class);
        } catch (ConstructorException skip) {
            try {
                container = yamlParser.parse(file, RuleContainer.WithOneRule.class)
                    .toContainer();
            } catch (ConstructorException skip2) {
                container = oldCompilation(file, skip);
            }
        }

        if(Objects.isNull(container)) {
            throw StubyamlException.of("Container can't be null, file=%s", file.getPath());
        }

        return container;
    }

    private RuleContainer oldCompilation(File file, ConstructorException skip) {
        RuleContainer container;
        Rule[] rules = (Rule[]) super.compile(file);
        try {
            container = RuleContainer.builder()
                .rules(Arrays.asList(rules))
                .version(Version.V10.name())
                .build();
        } catch (Exception e) {
            throw skip;
        }

        return container;
    }

}
