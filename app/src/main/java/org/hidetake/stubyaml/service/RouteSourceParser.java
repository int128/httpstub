package org.hidetake.stubyaml.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hidetake.stubyaml.model.yaml.*;
import org.hidetake.stubyaml.service.rules.CompositeRulesCompiler;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import static org.hidetake.stubyaml.util.StringUtils.firstNonEmpty;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteSourceParser {

    private final CompositeRulesCompiler compositeRulesCompiler;

    public RouteSource parse(File file) {
        //Small trade-off, for request mapping lookup
        RuleContainer container = readFile(file);
        return container.getRules().stream()
            .filter(rule -> Objects.nonNull(rule.getRequest()))
            .findFirst()
            .map(rule -> createCustomRouteSource(rule.getRequest(), file))
            .orElseGet(() -> new FilenameRouteSource(file));
    }

    private RouteSource createCustomRouteSource(Request request, File file) {
        return CustomRouteSource.builder()
            .source(new FilenameRouteSource(file))
            .relative(Objects.equals(request.getRelative(), Boolean.TRUE))
            .path(request.getPath())
            .method(firstNonEmpty(request.getMethod(), "GET"))
            .type(Route.RouteType.YAML)
            .build();
    }

    private RuleContainer readFile(File file) {
        try {
            return compositeRulesCompiler.compile(file);
        } catch (Exception e) {
            return RuleContainer.empty();
        }
    }

    public Optional<Route> convertToRoute(RouteSource routeSource, Path basePath) {
        final var relativePath = routeSource.computeRelativePath(routeSource.getFile(), basePath);
        return Optional.ofNullable(
            routeSource.parse(relativePath));
    }

}
