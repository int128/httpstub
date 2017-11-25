package org.hidetake.stubyaml.model;

import lombok.RequiredArgsConstructor;
import org.hidetake.stubyaml.model.execution.CompiledRoute;
import org.hidetake.stubyaml.model.yaml.Route;
import org.springframework.stereotype.Component;

import static java.util.stream.Collectors.toList;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;

@Component
@RequiredArgsConstructor
public class RouteCompiler {
    private final RuleCompiler ruleCompiler;

    public CompiledRoute compile(Route route) {
        notNull(route, "route should not be null");
        hasText(route.getRequestPath(), "request path should have text");
        notNull(route.getHttpMethod(), "request method should not be null");
        notNull(route.getRules(), "rules should not be null");

        return CompiledRoute.builder()
            .httpMethod(route.getHttpMethod())
            .requestPath(route.getRequestPath())
            .rules(route.getRules().stream().map(ruleCompiler::compile).collect(toList()))
            .build();
    }
}
