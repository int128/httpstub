package org.hidetake.stubyaml.model.execution;

import groovy.text.Template;
import lombok.Builder;
import lombok.Data;
import lombok.val;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class CompiledRule {
    private final CompiledExpression when;
    private final int status;
    private final Map<String, Template> headers;
    private final Template body;
    private final List<CompiledTable> tables;

    public boolean matches(RequestContext requestContext) {
        if (when == null) {
            return true;
        } else {
            val result = when.evaluate(requestContext.getBinding());
            return (result instanceof Boolean) && (Boolean) result;
        }
    }

    public ResponseEntity createResponseEntity(RequestContext requestContext) {
        val resolvedRequestContext = ResolvedRequestContext.resolve(tables, requestContext);

        val builder = ResponseEntity.status(status);
        headers.forEach((headerName, template) -> {
            val headerValue = template.make(resolvedRequestContext.getBinding()).toString();
            builder.header(headerName, headerValue);
        });
        if (body == null) {
            return builder.build();
        } else {
            return builder.body(body.make(resolvedRequestContext.getBinding()).toString());
        }
    }
}
