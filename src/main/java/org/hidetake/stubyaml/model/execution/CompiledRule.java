package org.hidetake.stubyaml.model.execution;

import lombok.Builder;
import lombok.Data;
import lombok.val;
import org.hidetake.stubyaml.model.RequestContext;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Data
@Builder
public class CompiledRule {
    private final int status;
    private final Map<String, Template> headers;
    private final Template body;

    public boolean matches(RequestContext requestContext) {
        return true;  //TODO
    }

    public ResponseEntity createResponseEntity(RequestContext requestContext) {
        val builder = ResponseEntity.status(status);
        headers.forEach((key, expression) -> builder.header(key, expression.evaluate(requestContext)));
        return builder.body(body.evaluate(requestContext));
    }
}
