package org.hidetake.stubyaml.model;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.hidetake.stubyaml.model.execution.CompiledRoute;
import org.hidetake.stubyaml.model.execution.CompiledRule;
import org.hidetake.stubyaml.model.yaml.Route;
import org.hidetake.stubyaml.model.yaml.RouteSource;
import org.hidetake.stubyaml.model.yaml.Rule;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.springframework.web.reactive.function.server.RequestPredicates.method;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;

@Component
@RequiredArgsConstructor
public class RouteCompiler {
    private final RuleCompiler ruleCompiler;
    private final Yaml yamlParser = new Yaml();

    public Optional<CompiledRoute> compile(RouteSource routeSource, File baseDirectory) {
        return routeSource.parseName(baseDirectory.toPath())
            .flatMap(route -> {
                switch (route.getType()) {
                    case YAML:
                        return Optional.of(CompiledRoute.builder()
                            .requestPredicate(requestPredicate(routeSource, route))
                            .rules(compileYaml(routeSource))
                            .build());
                    default:
                        return Optional.empty();
                }
            });
    }

    private List<CompiledRule> compileYaml(RouteSource routeSource) {
        return parseYaml(routeSource).stream()
            .map(rule -> ruleCompiler.compile(routeSource, rule))
            .collect(toList());
    }

    private List<Rule> parseYaml(RouteSource routeSource) {
        try (val yamlStream = new FileInputStream(routeSource.getFile())) {
            val rules = yamlParser.loadAs(yamlStream, Rule[].class);
            if (rules == null) {
                return emptyList();
            } else {
                return asList(rules);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (YAMLException e) {
            throw new IllegalRouteException(routeSource, e);
        }
    }

    private static RequestPredicate requestPredicate(RouteSource routeSource, Route route) {
        val httpMethodName = route.getMethod().toUpperCase();
        try {
            return method(HttpMethod.valueOf(httpMethodName)).and(path(route.getRequestPath()));
        } catch (IllegalArgumentException e) {
            throw new IllegalRouteException(routeSource, "Ignored invalid HTTP method: " + httpMethodName);
        }
    }
}
