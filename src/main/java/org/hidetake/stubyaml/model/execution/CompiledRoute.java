package org.hidetake.stubyaml.model.execution;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpMethod;

import java.util.List;

@Data
@Builder
public class CompiledRoute {
    private final HttpMethod httpMethod;
    private final String requestPath;
    private final List<CompiledRule> rules;
}
