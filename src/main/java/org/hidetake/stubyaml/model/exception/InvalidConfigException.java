package org.hidetake.stubyaml.model.exception;

import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;

public class InvalidConfigException extends RuntimeException {
    public InvalidConfigException(File configFile, YAMLException e) {
        super(String.format("Error in %s:\n%s", configFile, e));
    }
}
