package org.hidetake.stubyaml.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.File;

@Component
@RequiredArgsConstructor
public class YamlParser implements Parser {

    private final Loader loader;
    private final Yaml yamlParser;

    @Override
    @SneakyThrows
    public <T> T parse(File file, Class<T> clazz) {
        try (final var fileStream = loader.load(file)) {
            return yamlParser.loadAs(fileStream, clazz);
        }
    }

}
