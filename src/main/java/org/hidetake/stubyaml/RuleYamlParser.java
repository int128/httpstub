package org.hidetake.stubyaml;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.hidetake.stubyaml.model.RequestAndResponseRule;
import org.hidetake.stubyaml.model.Rule;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

@Slf4j
@Component
public class RuleYamlParser {
    public Stream<Rule> parse(File yamlFile, String requestPath, RequestMethod requestMethod) {
        try (val yamlStream = FileUtils.openInputStream(yamlFile)) {
            val rules = new Yaml().loadAs(yamlStream, RequestAndResponseRule[].class);

            log.info("yaml={}", Arrays.toString(rules));

            return Stream.of(new Rule(requestPath, requestMethod, rules));
        } catch (IOException e) {
            log.warn("Ignored YAML file {}", yamlFile, e);
            return Stream.empty();
        }
    }
}
