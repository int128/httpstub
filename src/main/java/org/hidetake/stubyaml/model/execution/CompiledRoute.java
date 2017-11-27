package org.hidetake.stubyaml.model.execution;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.reactive.function.server.RequestPredicate;

import java.util.List;

@Data
@Builder
public class CompiledRoute {
    private final RequestPredicate requestPredicate;
    private final List<CompiledRule> rules;
}
