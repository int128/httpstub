package org.hidetake.stubyaml.model.execution;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;

import java.time.Duration;

import static org.hidetake.stubyaml.util.MapUtils.mapMultiValue;
import static org.springframework.util.ObjectUtils.nullSafeToString;

@Data
@Builder
public class CompiledResponse {
    private final int status;
    private final MultiValueMap<String, CompiledExpression> headers;
    private final CompiledResponseBody<?> body;
    private final CompiledTables tables;
    private final Duration delay;

    public HttpStatus getHttpStatus() {
        return HttpStatus.valueOf(status);
    }

    public MultiValueMap<String, String> evaluateHeaders(ResponseContext responseContext) {
        return mapMultiValue(headers, expression ->
            nullSafeToString(expression.evaluate(responseContext)));
    }
}
