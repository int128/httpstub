package org.hidetake.stubyaml.app;

import lombok.RequiredArgsConstructor;
import org.hidetake.stubyaml.model.execution.CompiledRoute;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class RouteHandler {
    private static final Mono<ServerResponse> NO_RULE_MATCHED =
        ServerResponse.notFound().build();

    private final RequestExtractor requestExtractor;
    private final ResponseRenderer responseRenderer;

    public Mono<ServerResponse> handle(CompiledRoute route, ServerRequest request) {
        return requestExtractor
            .extract(request)
            .flatMap(requestContext ->
                route.findRule(requestContext)
                    .map(rule -> responseRenderer.render(rule.getResponse(), requestContext))
                    .orElse(NO_RULE_MATCHED));
    }

    public Proxy proxy(CompiledRoute route) {
        return new Proxy(route);
    }

    @RequiredArgsConstructor
    public class Proxy implements HandlerFunction<ServerResponse> {
        private final CompiledRoute route;

        @Override
        public Mono<ServerResponse> handle(ServerRequest request) {
            return RouteHandler.this.handle(route, request);
        }

        @Override
        public String toString() {
            return String.format("(%d rule(s))", route.getRules().size());
        }
    }
}
