package org.hidetake.stubyaml.app;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ReloadableHttpHandler implements HttpHandler {
    private static final HttpHandler INITIALIZING =
        RouterFunctions.toHttpHandler(
            RouterFunctions.route(
                RequestPredicates.all(),
                request -> ServerResponse
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .syncBody("Initializing...")));

    private volatile HttpHandler httpHandler = INITIALIZING;

    public void reload(RouterFunction<ServerResponse> routerFunction) {
        httpHandler = RouterFunctions.toHttpHandler(routerFunction);
    }

    @Override
    public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
        return httpHandler.handle(request, response);
    }
}
