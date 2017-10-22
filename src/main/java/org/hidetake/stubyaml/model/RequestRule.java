package org.hidetake.stubyaml.model;

import lombok.Data;

@Data
public class RequestRule {
    private final String path;
    private final String method;

    public static RequestRule fromFilename(String filename) {
    }
}
