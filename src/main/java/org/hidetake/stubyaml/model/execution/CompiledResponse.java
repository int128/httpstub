package org.hidetake.stubyaml.model.execution;

import lombok.Builder;
import lombok.Data;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.hidetake.stubyaml.util.MapUtils.mapValue;
import static org.springframework.util.ObjectUtils.nullSafeToString;

@Data
@Builder
public class CompiledResponse {
    private final int status;
    private final Map<String, CompiledExpression> headers;
    private final Object body;
    private final CompiledTables tables;
    private final long delay;

    public Mono<ServerResponse> render(RequestContext requestContext) {
        val binding = tables.resolve(requestContext);
        val builder = ServerResponse.status(HttpStatus.valueOf(status));
        headers.forEach((headerName, expression) -> {
            val headerValue = nullSafeToString(expression.evaluate(binding));
            builder.header(headerName, headerValue);
        });
        val renderedBody = renderBody(body, binding);

        waitForDelay();
        return builder.syncBody(renderedBody);
    }

    protected Object renderBody(Object body, Map<String, Object> binding) {
        if (body == null || body instanceof Number || body instanceof Boolean) {
            return body;
        } else if (body instanceof CompiledExpression) {
            val expression = (CompiledExpression) body;
            val value = expression.evaluate(binding);
            return renderBody(value, binding);
        } else if (body instanceof List) {
            val list = (List<?>) body;
            return list.stream().map(e -> renderBody(e, binding)).collect(toList());
        } else if (body instanceof Map) {
            val map = (Map<?, ?>) body;
            return mapValue(map, v -> renderBody(v, binding));
        } else {
            return body.toString();
        }
    }

    protected void waitForDelay() {
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
