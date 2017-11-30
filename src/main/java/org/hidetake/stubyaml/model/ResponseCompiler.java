package org.hidetake.stubyaml.model;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RequiredArgsConstructor
@Component
public class ResponseCompiler {
    private final ExpressionCompiler expressionCompiler;
    private final TableCompiler tableCompiler;

    public CompiledResponse compile(RouteSource source, Response response) {
        notNull(response, "response should not be null");
        notNull(response.getHeaders(), "headers should not be null");

        return CompiledResponse.builder()
            .status(response.getStatus())
            .headers(mapValue(response.getHeaders(), expressionCompiler::compileTemplate))
            .body(compileBody(source, response))
            .tables(tableCompiler.compile(response.getTables()))
            .delay(computeDelay(response))
            .build();
    }

    private CompiledResponseBody compileBody(RouteSource source, Response response) {
        val body = response.getBody();
        val file = response.getFile();
        if (body != null && file != null) {
            throw new IllegalStateException("Either body or file must be provided");
        }
        if (body == null && file == null) {
            return new CompiledResponseBody.NullBody();
        }
        if (body != null) {
            return new CompiledResponseBody.PrimitiveBody(compilePrimitiveBody(body));
        } else {
            val filenameExpression = expressionCompiler.compileTemplate(file);
            val baseDirectory = source.getYamlFile().getParentFile();
            return new CompiledResponseBody.FileBody(filenameExpression, baseDirectory);
        }
    }

    private Object compilePrimitiveBody(Object body) {
        if (body instanceof String) {
            val string = (String) body;
            return expressionCompiler.compileTemplate(string);
        } else if (body instanceof List) {
            val list = (List<?>) body;
            return list.stream().map(this::compilePrimitiveBody).collect(toList());
        } else if (body instanceof Map) {
            val map = (Map<?, ?>) body;
            return mapValue(map, this::compilePrimitiveBody);
        } else {
            return body;
        }
    }

    private Duration computeDelay(Response response) {
        val delay = response.getDelay();
        if (delay >= 0) {
            return Duration.ofMillis(delay);
        } else {
            log.warn("Ignored invalid delay {}", delay);
            return Duration.ZERO;
        }
    }
}
