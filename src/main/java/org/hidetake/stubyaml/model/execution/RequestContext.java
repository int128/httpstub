package org.hidetake.stubyaml.model.execution;

import groovy.lang.Binding;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.val;

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
    private final Binding binding = createBinding();

    Binding createBinding() {
        val binding = new Binding();
        binding.setVariable("headers", requestHeaders);
        binding.setVariable("path", pathVariables);
        binding.setVariable("params", requestParams);
        binding.setVariable("body", requestBody);
        binding.setVariable("constants", constants);
        return binding;
    };
}
