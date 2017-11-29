package org.hidetake.stubyaml.model.execution;

import lombok.Builder;
import lombok.Data;
import lombok.val;
import org.hidetake.stubyaml.util.MapUtils;
import org.springframework.http.HttpStatus;

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

    public HttpStatus getHttpStatus() {
        return HttpStatus.valueOf(status);
    }

    public Map<String, String> renderHeaders(ResponseContextMap responseContextMap) {
        return MapUtils.mapValue(headers, expression ->
            nullSafeToString(expression.evaluate(responseContextMap)));
    }

    public Object renderBody(ResponseContextMap responseContextMap) {
        return renderNestedBody(body, responseContextMap);
    }

    private static Object renderNestedBody(Object body, ResponseContextMap responseContextMap) {
        if (body == null || body instanceof Number || body instanceof Boolean) {
            return body;
        } else if (body instanceof CompiledExpression) {
            val expression = (CompiledExpression) body;
            val value = expression.evaluate(responseContextMap);
            return renderNestedBody(value, responseContextMap);
        } else if (body instanceof List) {
            val list = (List<?>) body;
            return list.stream().map(e -> renderNestedBody(e, responseContextMap)).collect(toList());
        } else if (body instanceof Map) {
            val map = (Map<?, ?>) body;
            return mapValue(map, v -> renderNestedBody(v, responseContextMap));
        } else {
            return body.toString();
        }
    }
}
