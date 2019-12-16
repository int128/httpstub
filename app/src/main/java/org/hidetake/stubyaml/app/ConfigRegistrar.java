package org.hidetake.stubyaml.app;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hidetake.stubyaml.model.ConfigCompiler;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConfigRegistrar {

    private final ConfigHolder configHolder;
    private final ConfigCompiler configCompiler;

    //TODO: #RESOURCE_TAG change folder to new class Resource(it could be local or remote file)
    public void register(File baseDirectory) {
        final var configFile = parseConfig(baseDirectory);
        if (configFile.exists()) {
            try {
                final var config = configCompiler.compile(configFile);
                configHolder.setConfig(config);
            } catch (Exception e) {
                log.warn("Ignored invalid config: {}", e.toString());
                configHolder.reset();
            }
        } else {
            log.info("Config {} does not exist, falling back to defaults", configFile);
            configHolder.reset();
        }
    }

    private File parseConfig(File baseDirectory) {
        File yaml = new File(baseDirectory, "config.yaml");
        File yml = new File(baseDirectory, "config.yml");

        return !yaml.exists() ? yml : yaml;
    }

}
