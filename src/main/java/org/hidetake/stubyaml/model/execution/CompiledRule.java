package org.hidetake.stubyaml.model.execution;

import groovy.lang.Binding;
import groovy.text.Template;
import lombok.Builder;
import lombok.Data;
import lombok.val;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Data
@Builder
public class CompiledRule {
    private final Expression when;
    private final int status;
    private final Map<String, Template> headers;
    private final Template body;

    public boolean matches(RequestContext requestContext) {
        if (when == null) {
            return true;
        } else {
            val result = when.evaluate(new Binding(requestContext.getBinding()));
            return (result instanceof Boolean) && (Boolean) result;
        }
    }

    public ResponseEntity createResponseEntity(RequestContext requestContext) {
        val builder = ResponseEntity.status(status);
        headers.forEach((key, expression) -> {
            val value = expression.make(requestContext.getBinding()).toString();
            builder.header(key, value);
        });
        if (body == null) {
            return builder.build();
        } else {
            return builder.body(body.make(requestContext.getBinding()).toString());
        }
    }
}
