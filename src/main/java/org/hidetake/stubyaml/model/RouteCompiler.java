package org.hidetake.stubyaml.model;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.hidetake.stubyaml.model.execution.CompiledRoute;
import org.hidetake.stubyaml.model.execution.CompiledRule;
import org.hidetake.stubyaml.model.yaml.Route;
import org.hidetake.stubyaml.model.yaml.RouteSource;
import org.hidetake.stubyaml.service.ObjectCompiler;
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

    //TODO: #JSON_TAG add json support
    public Optional<CompiledRoute> compile(RouteSource routeSource, File baseDirectory) {
        return routeSource.parseName(baseDirectory.toPath())
            .flatMap(route -> {
                if (Route.RouteType.YAML == route.getType()) {
                    return Optional.of(CompiledRoute.builder()
                        .requestPredicate(requestPredicate(routeSource, route))
                        .rules(compileYaml(routeSource))
                        .build());
                }
                return Optional.empty();
            });
    }

    private List<CompiledRule> compileRules(RouteSource routeSource) {
        return rulesCompiler.compile(routeSource);
    }

    @SneakyThrows
    private RequestPredicate createRequestPredicate(Route route) {
        final var httpMethod = HttpMethod.valueOf(route.getMethod().toUpperCase());

        return method(httpMethod)
            .and(path(route.getRequestPath()));
    }

}
