package org.hidetake.stubyaml.app;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hidetake.stubyaml.model.execution.CompiledRoute;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
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
}
