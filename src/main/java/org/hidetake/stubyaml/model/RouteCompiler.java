package org.hidetake.stubyaml.model;

import lombok.RequiredArgsConstructor;
import org.hidetake.stubyaml.model.execution.CompiledRoute;
import org.hidetake.stubyaml.model.yaml.Route;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

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
        notNull(route.getRequestMethod(), "request method should not be null");
        notNull(route.getRules(), "rules should not be null");

        return CompiledRoute.builder()
            .requestMappingInfo(new RequestMappingInfo(
                new PatternsRequestCondition(route.getRequestPath()),
                new RequestMethodsRequestCondition(route.getRequestMethod()),
                null,
                null,
                null,
                null,
                null
            ))
            .rules(route.getRules().stream().map(ruleCompiler::compile).collect(toList()))
            .build();
    }
}
