package org.hidetake.stubyaml;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.hidetake.stubyaml.model.execution.CompiledRoute;
import org.hidetake.stubyaml.model.execution.RequestContext;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@RequiredArgsConstructor
public class RouteHandler implements HandlerFunction<ServerResponse> {
    private final CompiledRoute route;

    public Mono<ServerResponse> handle(ServerRequest request) {
        val requestContext = RequestContext.builder()
            .request(request)
            .requestHeaders(request.headers().asHttpHeaders().toSingleValueMap())
            .pathVariables(request.pathVariables())
            .requestParams(request.queryParams().toSingleValueMap())
            .requestBody(request.bodyToMono(Map.class))
            .build();
        return route.getRules().stream()
            .filter(rule -> rule.matches(requestContext))
            .findFirst()
            .map(rule -> rule.getResponse().render(requestContext))
            .orElseGet(() -> ServerResponse.notFound().build());
    }
}
