package org.hidetake.stubyaml.model.execution;

import lombok.Builder;
import lombok.Data;
import lombok.val;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.hidetake.stubyaml.util.MapUtils.mapValue;

@Data
@Builder
public class CompiledResponse {
    private final int status;
    private final Map<String, CompiledTemplate> headers;
    private final Object body;
    private final CompiledTables tables;

    public ResponseEntity render(RequestContext requestContext) {
        val binding = tables.resolve(requestContext);

        val builder = ResponseEntity.status(status);
        headers.forEach((headerName, template) -> {
            val headerValue = template.build(binding);
            builder.header(headerName, headerValue);
        });
        if (body == null) {
            return builder.build();
        } else {
            return builder.body(renderBody(body, binding));
        }
    }

    protected Object renderBody(Object body, Map<String, Object> binding) {
        if (body instanceof CompiledTemplate) {
            val template = (CompiledTemplate) body;
            return template.build(binding);
        } else if (body instanceof List) {
            val list = (List<?>) body;
            return list.stream().map(e -> renderBody(e, binding)).collect(toList());
        } else if (body instanceof Map) {
            val map = (Map<?, ?>) body;
            return mapValue(map, v -> renderBody(v, binding));
        } else {
            return body;
        }
    }
}
