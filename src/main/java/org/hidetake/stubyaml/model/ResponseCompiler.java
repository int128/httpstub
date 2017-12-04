package org.hidetake.stubyaml.model;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.hidetake.stubyaml.model.execution.CompiledResponse;
import org.hidetake.stubyaml.model.execution.CompiledResponseBody;
import org.hidetake.stubyaml.model.yaml.Response;
import org.hidetake.stubyaml.model.yaml.RouteSource;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.hidetake.stubyaml.util.MapUtils.mapValue;
import static org.springframework.util.Assert.notNull;

@RequiredArgsConstructor
@Component
public class ResponseCompiler {
    private final ExpressionCompiler expressionCompiler;
    private final TableCompiler tableCompiler;

    public CompiledResponse compile(Response response, RouteSource source) {
        notNull(response, "response should not be null");
        notNull(response.getHeaders(), "headers should not be null");

        return CompiledResponse.builder()
            .status(response.getStatus())
            .headers(mapValue(response.getHeaders(), value -> expressionCompiler.compileTemplate(value, source)))
            .body(compileBody(response, source))
            .tables(tableCompiler.compile(response.getTables(), source))
            .delay(computeDelay(response, source))
            .build();
    }

    private CompiledResponseBody compileBody(Response response, RouteSource source) {
        val body = response.getBody();
        val file = response.getFile();
        if (body != null && file != null) {
            throw new IllegalStateException("Either body or file must be provided");
        }
        if (body == null && file == null) {
            return new CompiledResponseBody.NullBody();
        }
        if (body != null) {
            return new CompiledResponseBody.PrimitiveBody(compilePrimitiveBody(body, source));
        } else {
            val filenameExpression = expressionCompiler.compileTemplate(file, source);
            val baseDirectory = source.getFile().getParentFile();
            return new CompiledResponseBody.FileBody(filenameExpression, baseDirectory);
        }
    }

    private Object compilePrimitiveBody(Object body, RouteSource source) {
        if (body instanceof String) {
            val string = (String) body;
            return expressionCompiler.compileTemplate(string, source);
        } else if (body instanceof List) {
            val list = (List<?>) body;
            return list.stream().map(item -> compilePrimitiveBody(item, source)).collect(toList());
        } else if (body instanceof Map) {
            val map = (Map<?, ?>) body;
            return mapValue(map, item -> compilePrimitiveBody(item, source));
        } else {
            return body;
        }
    }

    private Duration computeDelay(Response response, RouteSource source) {
        val delay = response.getDelay();
        if (delay >= 0) {
            return Duration.ofMillis(delay);
        } else {
            throw new IllegalRuleException("Invalid delay: " + delay, source);
        }
    }
}
