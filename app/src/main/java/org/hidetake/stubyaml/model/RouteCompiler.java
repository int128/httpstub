package org.hidetake.stubyaml.model;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.hidetake.stubyaml.model.exception.IllegalRouteException;
import org.hidetake.stubyaml.model.execution.CompiledRoute;
import org.hidetake.stubyaml.model.execution.CompiledRule;
import org.hidetake.stubyaml.model.yaml.Route;
import org.hidetake.stubyaml.model.yaml.RouteSource;
import org.hidetake.stubyaml.service.ObjectCompiler;
import org.hidetake.stubyaml.service.RouteSourceParser;
import org.hidetake.stubyaml.service.RulesCompiler;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicate;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static org.springframework.web.reactive.function.server.RequestPredicates.method;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;

@Component
@RequiredArgsConstructor
public class RouteCompiler implements ObjectCompiler {

    private final RulesCompiler rulesCompiler;
    private final RouteSourceParser routeSourceParser;


    //TODO: #JSON_TAG add json support
    public Optional<CompiledRoute> compile(RouteSource routeSource, File baseDirectory) {
        return routeSourceParser.convertToRoute(routeSource, baseDirectory.toPath())
            .flatMap(route -> {
                switch (route.getType()) {
                    case YAML:
                    case YML:
                        return Optional.of(CompiledRoute.builder()
                            .requestPredicate(requestPredicate(routeSource, route))
                            .rules(compileRules(routeSource))
                            .build());
                    default:
                        return Optional.empty();
                }
            });
    }

    private List<CompiledRule> compileRules(RouteSource routeSource) {
        return rulesCompiler.compile(routeSource);
    }

    @SneakyThrows
    private RequestPredicate requestPredicate(RouteSource routeSource, Route route) {
        final var httpMethodName = route.getMethod().toUpperCase();
        try {
            return method(HttpMethod.valueOf(httpMethodName)).and(path(route.getRequestPath()));
        } catch (IllegalArgumentException e) {
            throw new IllegalRouteException(routeSource, "Ignored invalid HTTP method: " + httpMethodName);
        }
    }

}
