package org.hidetake.stubyaml.model;

import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;

public class InvalidConfigException extends RuntimeException {
    public InvalidConfigException(File configFile, YAMLException e) {
        super("Invalid config file: " + configFile, e);
    }
}
