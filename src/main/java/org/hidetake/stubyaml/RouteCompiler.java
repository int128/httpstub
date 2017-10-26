package org.hidetake.stubyaml;

import lombok.RequiredArgsConstructor;
import org.hidetake.stubyaml.model.execution.CompiledRoute;
import org.hidetake.stubyaml.model.yaml.Route;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class RouteCompiler {
    private final RuleCompiler ruleCompiler;

    public CompiledRoute compile(Route route) {
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
            .rules(route.getRules().stream()
                .map(ruleCompiler::compile)
                .collect(toList()))
            .build();
    }
}
