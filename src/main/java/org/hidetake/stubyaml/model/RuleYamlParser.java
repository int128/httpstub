package org.hidetake.stubyaml.model;

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
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

@Slf4j
@Component
public class RuleYamlParser {
    public Route parse(File yamlFile, String requestPath, RequestMethod requestMethod) {
        return new Route(requestPath, requestMethod, parse(yamlFile));
    }

    private List<Rule> parse(File yamlFile) {
        try (val yamlStream = FileUtils.openInputStream(yamlFile)) {
            return asList(new Yaml().loadAs(yamlStream, Rule[].class));
        } catch (IOException e) {
            log.warn("Ignored YAML file {}", yamlFile, e);
            return emptyList();
        }
    }
}
