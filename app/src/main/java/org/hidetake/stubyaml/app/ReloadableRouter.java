package org.hidetake.stubyaml.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.web.reactive.function.server.RequestPredicates.all;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

@Slf4j
@Component
public class ReloadableRouter implements RouterFunction<ServerResponse> {
    private volatile RouterFunction<ServerResponse> current =
        RouterFunctions.route(
            all(), request -> status(INTERNAL_SERVER_ERROR).syncBody("Initializing..."));

    public void reload(RouterFunction<ServerResponse> routerFunction) {
        log.info("Reloading router function... {} -> {}", current, routerFunction);
        current = routerFunction;
    }

    @Override
    public Mono<HandlerFunction<ServerResponse>> route(ServerRequest request) {
        return current.route(request);
    }
}
