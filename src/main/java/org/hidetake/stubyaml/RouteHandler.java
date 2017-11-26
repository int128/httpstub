package org.hidetake.stubyaml;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.hidetake.stubyaml.model.execution.CompiledRoute;
import org.hidetake.stubyaml.model.execution.RequestContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;

@Data
@RequiredArgsConstructor
public class RouteHandler implements HandlerFunction<ServerResponse> {
    private static final ParameterizedTypeReference<Map<String, Object>> MAP_TYPE =
        new ParameterizedTypeReference<Map<String, Object>>() {};

    private final CompiledRoute route;

    public Mono<ServerResponse> handle(ServerRequest request) {
        val requestContextBuilder = RequestContext.builder()
            .request(request)
            .requestHeaders(request.headers().asHttpHeaders().toSingleValueMap())
            .pathVariables(request.pathVariables())
            .requestParams(request.queryParams().toSingleValueMap());

        return extractBody(request)
            .map(body -> requestContextBuilder.requestBody(body).build())
            .switchIfEmpty(Mono.just(requestContextBuilder.build()))
            .flatMap(requestContext ->
                route.getRules().stream()
                    .filter(rule -> rule.matches(requestContext))
                    .findFirst()
                    .map(rule -> rule.getResponse().render(requestContext))
                    .orElseGet(() -> ServerResponse.notFound().build()));
    }

    private Mono<?> extractBody(ServerRequest request) {
        return request.headers().contentType().map(mediaType -> {
            if (APPLICATION_FORM_URLENCODED.includes(mediaType)) {
                return request.body(BodyExtractors.toFormData()).map(MultiValueMap::toSingleValueMap);
            } else if (MULTIPART_FORM_DATA.includes(mediaType)) {
                return request.body(BodyExtractors.toMultipartData()).map(MultiValueMap::toSingleValueMap);
            } else {
                return request.bodyToMono(MAP_TYPE);
            }
        }).orElseGet(() ->
            request.bodyToMono(MAP_TYPE));
    }
}
