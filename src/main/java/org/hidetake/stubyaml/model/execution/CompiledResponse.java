package org.hidetake.stubyaml.model.execution;

import lombok.Builder;
import lombok.Data;
import org.hidetake.stubyaml.util.MapUtils;
import org.springframework.http.HttpStatus;

import java.time.Duration;
import java.util.Map;

import static org.springframework.util.ObjectUtils.nullSafeToString;

@Data
@Builder
public class CompiledResponse {
    private final int status;
    private final Map<String, CompiledExpression> headers;
    private final CompiledResponseBody body;
    private final CompiledTables tables;
    private final Duration delay;

    public HttpStatus getHttpStatus() {
        return HttpStatus.valueOf(status);
    }

    public Map<String, String> evaluateHeaders(ResponseContext responseContext) {
        return MapUtils.mapValue(headers, expression ->
            nullSafeToString(expression.evaluate(responseContext)));
    }
}
