package org.hidetake.stubyaml.model.execution;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.reactive.function.server.RequestPredicate;

import java.util.List;
import java.util.Optional;

@Data
@Builder
public class CompiledRoute {

    private final RequestPredicate requestPredicate;
    private final List<CompiledRule> rules;

    // TODO: change findFirst to something with order
    public Optional<CompiledRule> findRule(RequestContext requestContext) {
        return rules.stream()
            .filter(rule -> rule.matches(requestContext))
            .findFirst();
    }

}
