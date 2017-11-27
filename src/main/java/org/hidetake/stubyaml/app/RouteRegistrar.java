package org.hidetake.stubyaml.app;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hidetake.stubyaml.model.RouteCompiler;
import org.hidetake.stubyaml.model.RouteScanner;
import org.hidetake.stubyaml.model.execution.CompiledRoute;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.io.File;
import java.io.IOException;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

@Slf4j
@RequiredArgsConstructor
@Component
public class RouteRegistrar {
    private final ReloadableHttpHandler reloadableHttpHandler;
    private final RouteScanner routeScanner;
    private final RouteCompiler routeCompiler;

    public void register(File baseDirectory) {
        reloadableHttpHandler.reload(scan(baseDirectory));
    }

    public RouterFunction<ServerResponse> scan(File baseDirectory) {
        if (baseDirectory.isDirectory()) {
            if (!ObjectUtils.isEmpty(baseDirectory.listFiles())) {
                try {
                    return routeScanner.scan(baseDirectory)
                        .map(routeCompiler::compile)
                        .map(route -> route(predicate(route), new RouteHandler(route)))
                        .reduce(RouterFunction::and)
                        .orElseGet(() -> route(all(), req -> status(HttpStatus.INTERNAL_SERVER_ERROR).syncBody("error")));
                } catch (IOException e) {
                    log.warn("Could not scan directory {}", baseDirectory.getAbsolutePath(), e);
                    return route(all(), req -> status(HttpStatus.INTERNAL_SERVER_ERROR).syncBody("error"));
                }
            } else {
                log.warn("No rule found in {}", baseDirectory.getAbsolutePath());
                return route(all(), req -> status(HttpStatus.INTERNAL_SERVER_ERROR).syncBody("error"));
            }
        } else {
            log.warn("Not found directory {}", baseDirectory.getAbsolutePath());
            return route(all(), req -> status(HttpStatus.INTERNAL_SERVER_ERROR).syncBody("error"));
        }
    }

    private static RequestPredicate predicate(CompiledRoute route) {
        return method(route.getHttpMethod()).and(path(route.getRequestPath()));
    }
}
