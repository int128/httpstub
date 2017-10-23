package org.hidetake.stubyaml.model;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.File;

@Data
public class Rule {
    private final String path;
    private final RequestMethod method;
    private final File yamlFile;
}
