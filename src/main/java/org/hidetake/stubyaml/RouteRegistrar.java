package org.hidetake.stubyaml;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hidetake.stubyaml.model.RouteCompiler;
import org.hidetake.stubyaml.model.RouteScanner;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

@Slf4j
@RequiredArgsConstructor
@Component
public class RouteRegistrar {
    private final RouteScanner routeScanner;
    private final RouteCompiler routeCompiler;

    public RouterFunction<ServerResponse> register(File baseDirectory) {
        if (baseDirectory.isDirectory()) {
            if (!ObjectUtils.isEmpty(baseDirectory.listFiles())) {
                try {
                    return routeScanner.scan(baseDirectory)
                        .map(routeCompiler::compile)
                        .map(route -> route(
                            method(route.getHttpMethod()).and(path(route.getRequestPath())),
                            new RouteHandler(route)))
                        .reduce(
                            route(GET("/"), req -> ok().body(Flux.just("OK"), String.class)),
                            RouterFunction::and);
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

//    public void unregister(RequestMappingHandlerMapping mapping) {
//        new ArrayList<>(mapping.getHandlerMethods().keySet())
//            .forEach(requestMappingInfo -> {
//                log.info("Unregistering {}", requestMappingInfo);
//                mapping.unregisterMapping(requestMappingInfo);
//            });
//    }
}
