package org.hidetake.stubyaml.model;

import lombok.val;
import org.hidetake.stubyaml.model.yaml.Config;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Component
public class ConfigCompiler {
    private final Yaml yamlParser = new Yaml();

    public Config compile(File configFile) {
        return parseYaml(configFile);
    }

    private Config parseYaml(File configFile) {
        try (val yamlStream = new FileInputStream(configFile)) {
            return yamlParser.loadAs(yamlStream, Config.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (YAMLException e) {
            throw new InvalidConfigException(configFile, e);
        }
    }
}
