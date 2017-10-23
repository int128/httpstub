package org.hidetake.stubyaml.model;

import lombok.Data;

@Data
public class Rule {
    private final String path;
    private final String method;

    public static Rule fromFilename(String filename) {
        return null;
    }
}
