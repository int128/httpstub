package org.hidetake.stubyaml.model.execution;

import lombok.Data;

@Data
public class Template {
    private final groovy.text.Template template;

    public String evaluate(RequestContext requestContext) {
        return template.make(requestContext.getBinding()).toString();
    }
}
