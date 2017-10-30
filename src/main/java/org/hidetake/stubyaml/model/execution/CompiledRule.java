package org.hidetake.stubyaml.model.execution;

import groovy.lang.Binding;
import groovy.lang.Script;
import groovy.text.Template;
import lombok.Builder;
import lombok.Data;
import lombok.val;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Data
@Builder
public class CompiledRule {
    private final Script when;
    private final int status;
    private final Map<String, Template> headers;
    private final Template body;

    public boolean matches(RequestContext requestContext) {
        if (when == null) {
            return true;
        } else {
            when.setBinding(new Binding(requestContext.getBinding()));
            val result = when.run();
            return (result instanceof Boolean) && (Boolean) result;
        }
    }

    public ResponseEntity createResponseEntity(RequestContext requestContext) {
        if (body == null) {
            return null;
        } else {
            val builder = ResponseEntity.status(status);
            headers.forEach((key, expression) -> {
                val value = expression.make(requestContext.getBinding()).toString();
                builder.header(key, value);
            });
            val body = this.body.make(requestContext.getBinding()).toString();
            return builder.body(body);
        }
    }
}
