package org.hidetake.stubyaml.model;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.hidetake.stubyaml.model.yaml.Rule;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

@Slf4j
@Component
public class RuleParser {
    private final Yaml yaml = new Yaml();

    public List<Rule> parse(File yamlFile) {
        try (val yamlStream = new FileInputStream(yamlFile)) {
            val rules = yaml.loadAs(yamlStream, Rule[].class);
            if (rules == null) {
                log.warn("No rules found in YAML file {}", yamlFile);
                return emptyList();
            } else {
                return asList(rules);
            }
        } catch (IOException e) {
            log.warn("Ignored YAML file {}", yamlFile, e);
            return emptyList();
        } catch (YAMLException e) {
            log.error("Ignored invalid YAML file {}\n{}", yamlFile, e.toString());
            return emptyList();
        }
    }
}
