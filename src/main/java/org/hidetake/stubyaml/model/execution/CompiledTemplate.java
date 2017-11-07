package org.hidetake.stubyaml.model.execution;

import groovy.text.Template;
import lombok.Data;

@Data
public class CompiledTemplate {
    private final Template template;

    public String build(ResolvedRequestContext resolvedRequestContext) {
        return template.make(resolvedRequestContext.getBinding()).toString();
    }
}
