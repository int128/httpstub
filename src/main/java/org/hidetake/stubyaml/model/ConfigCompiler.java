package org.hidetake.stubyaml.model;

import lombok.RequiredArgsConstructor;
import org.hidetake.stubyaml.model.yaml.Config;
import org.hidetake.stubyaml.service.ObjectCompiler;
import org.hidetake.stubyaml.service.Parser;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
public class ConfigCompiler implements ObjectCompiler {

    private final Parser parser;

    public Config compile(File configFile) {
        return parser.parse(configFile, Config.class);
    }

}
