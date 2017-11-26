package org.hidetake.stubyaml;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.hidetake.stubyaml.model.execution.CompiledRoute;
import org.hidetake.stubyaml.model.execution.RequestContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Data
@RequiredArgsConstructor
public class RouteHandler implements HandlerFunction<ServerResponse> {
    private final CompiledRoute route;

    public Mono<ServerResponse> handle(ServerRequest request) {
        val requestContextBuilder = RequestContext.builder()
            .request(request)
            .requestHeaders(request.headers().asHttpHeaders().toSingleValueMap())
            .pathVariables(request.pathVariables())
            .requestParams(request.queryParams().toSingleValueMap());

        return request.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .map(body -> requestContextBuilder.requestBody(body).build())
            .switchIfEmpty(Mono.just(requestContextBuilder.build()))
            .flatMap(requestContext ->
                route.getRules().stream()
                    .filter(rule -> rule.matches(requestContext))
                    .findFirst()
                    .map(rule -> rule.getResponse().render(requestContext))
                    .orElseGet(() -> ServerResponse.notFound().build()));
    }
}
