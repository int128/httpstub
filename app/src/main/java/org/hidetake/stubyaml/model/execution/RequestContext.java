package org.hidetake.stubyaml.model.execution;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Builder
public class RequestContext implements Bindable {

    private final Map<String, String> requestHeaders;
    private final Map<String, String> pathVariables;
    private final Map<String, String> requestParams;
    private final Object requestBody;
    private final Map<String, Object> constants;

    @Getter(lazy = true)
    private final Map binding = createBinding();

    public Map createBinding() {
        final var output = new LinkedHashMap<>();
        output.put("headers", requestHeaders);
        output.put("path", pathVariables);
        output.put("params", requestParams);
        output.put("body", requestBody);
        output.put("constants", constants);

        return output;
    }

}
