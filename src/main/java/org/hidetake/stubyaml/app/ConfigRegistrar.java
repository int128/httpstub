package org.hidetake.stubyaml.app;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.hidetake.stubyaml.model.ConfigCompiler;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@RequiredArgsConstructor
@Component
public class ConfigRegistrar {
    private final ConfigHolder configHolder;
    private final ConfigCompiler configCompiler;

    public void register(File baseDirectory){
        val configFile = new File(baseDirectory, "config.yaml");
        if (configFile.exists()) {
            try {
                val config = configCompiler.compile(configFile);
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
}
