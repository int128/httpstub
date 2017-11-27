package org.hidetake.stubyaml.app;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.hidetake.stubyaml.model.execution.CompiledRoute;
import org.hidetake.stubyaml.model.execution.RequestContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Map;

import static org.springframework.http.MediaType.*;

@RequiredArgsConstructor
@Component
public class RouteHandler {
    private static final ParameterizedTypeReference<Map<String, Object>> MAP_TYPE =
        new ParameterizedTypeReference<Map<String, Object>>() {};

    private static final MediaType TEXT_ALL = MediaType.valueOf("text/*");

    private final XmlMapper xmlMapper = new XmlMapper();

    public Mono<ServerResponse> handle(CompiledRoute route, ServerRequest request) {
        val requestContextBuilder = RequestContext.builder()
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

    public Mono<?> extractBody(ServerRequest request) {
        return request.headers().contentType().map(mediaType -> {
            if (APPLICATION_FORM_URLENCODED.includes(mediaType)) {
                return request.body(BodyExtractors.toFormData()).map(MultiValueMap::toSingleValueMap);
            } else if (MULTIPART_FORM_DATA.includes(mediaType)) {
                return request.body(BodyExtractors.toMultipartData()).map(MultiValueMap::toSingleValueMap);
            } else if (APPLICATION_JSON.includes(mediaType)) {
                return request.bodyToMono(MAP_TYPE);
            } else if (APPLICATION_XML.includes(mediaType)) {
                return extractBodyAsXml(request);
            } else if (TEXT_ALL.includes(mediaType)) {
                return extractBodyAsString(request);
            } else {
                return extractBodyAsBytes(request);
            }
        }).orElseGet(() -> extractBodyAsBytes(request));
    }

    public Mono<String> extractBodyAsString(ServerRequest request) {
        return request.bodyToMono(String.class);
    }

    public Mono<Map> extractBodyAsXml(ServerRequest request) {
        return extractBodyAsString(request).map(body -> {
            try {
                return xmlMapper.readValue(body, Map.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Mono<Void> extractBodyAsBytes(ServerRequest request) {
        // TODO: provide bytes to script
        return Mono.empty();
    }
}
