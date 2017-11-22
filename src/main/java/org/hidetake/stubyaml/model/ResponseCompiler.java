package org.hidetake.stubyaml.model;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.hidetake.stubyaml.model.execution.CompiledResponse;
import org.hidetake.stubyaml.model.yaml.Response;
import org.springframework.stereotype.Component;

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

    public CompiledResponse compile(Response response) {
        notNull(response, "response should not be null");
        notNull(response.getHeaders(), "headers should not be null");

        return CompiledResponse.builder()
            .status(response.getStatus())
            .headers(mapValue(response.getHeaders(), expressionCompiler::compileTemplate))
            .body(compileBody(response.getBody()))
            .tables(tableCompiler.compile(response.getTables()))
            .delay(computeDelay(response))
            .build();
    }

    protected Object compileBody(Object body) {
        if (body instanceof String) {
            val string = (String) body;
            return expressionCompiler.compileTemplate(string);
        } else if (body instanceof List) {
            val list = (List<?>) body;
            return list.stream().map(this::compileBody).collect(toList());
        } else if (body instanceof Map) {
            val map = (Map<?, ?>) body;
            return mapValue(map, this::compileBody);
        } else {
            return body;
        }
    }

    protected long computeDelay(Response response) {
        val delay = response.getDelay();
        if (delay >= 0) {
            return delay;
        } else {
            log.warn("Ignored invalid delay {}", delay);
            return 0;
        }
    }
}
