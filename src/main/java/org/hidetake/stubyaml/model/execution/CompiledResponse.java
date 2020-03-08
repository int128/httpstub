package org.hidetake.stubyaml.model.execution;

import lombok.Builder;
import lombok.Data;
import org.hidetake.stubyaml.util.MapUtils;
import org.springframework.http.HttpStatus;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.nullSafeToString;

@Data
@Builder
public class CompiledResponse {
    private final int status;
    private final Map<String, List<CompiledExpression>> headers;
    private final CompiledResponseBody body;
    private final CompiledTables tables;
    private final Duration delay;

    public HttpStatus getHttpStatus() {
        return HttpStatus.valueOf(status);
    }

    public Map<String, List<String>> evaluateHeaders(ResponseContext responseContext) {
        return MapUtils.mapValue(headers, expression ->
            expression.stream().map(
                v -> nullSafeToString(v.evaluate(responseContext))).collect(Collectors.toList()));
    }
}
