package org.hidetake.stubyaml.model.execution;

import groovy.text.Template;
import lombok.Data;

import java.util.Map;

@Data
public class CompiledTemplate {
    private final Template template;

    public String build(Map<String, Object> binding) {
        return template.make(binding).toString();
    }
}
