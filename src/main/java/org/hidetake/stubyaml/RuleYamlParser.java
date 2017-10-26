package org.hidetake.stubyaml;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.hidetake.stubyaml.model.yaml.Route;
import org.hidetake.stubyaml.model.yaml.Rule;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class RuleYamlParser {
    public Route parse(File yamlFile, String requestPath, RequestMethod requestMethod) {
        val rules = parse(yamlFile).collect(Collectors.toList());
        return new Route(requestPath, requestMethod, rules);
    }

    public Stream<Rule> parse(File yamlFile) {
        try (val yamlStream = FileUtils.openInputStream(yamlFile)) {
            return Stream.of(new Yaml().loadAs(yamlStream, Rule[].class));
        } catch (IOException e) {
            log.warn("Ignored YAML file {}", yamlFile, e);
            return Stream.empty();
        }
    }
}
