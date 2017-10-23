package org.hidetake.stubyaml.model;

import lombok.Data;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.io.File;

@Data
public class Rule {
    private final RequestMappingInfo requestMappingInfo;
    private final File yamlFile;
}
