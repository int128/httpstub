package org.hidetake.stubyaml.app;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.hidetake.stubyaml.model.execution.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

import static org.springframework.http.MediaType.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class RouteHandler {
    private static final ParameterizedTypeReference<Map<String, Object>> MAP_TYPE =
        new ParameterizedTypeReference<Map<String, Object>>() {};

    private static final MediaType TEXT_ALL = MediaType.valueOf("text/*");

    private final RequestResponseLogger requestResponseLogger;
    private final XmlMapper xmlMapper = new XmlMapper();

    public Mono<ServerResponse> handle(CompiledRoute route, ServerRequest request) {
        requestResponseLogger.logRequestHeaders(request);

        val requestContextBuilder = RequestContext.builder()
            .requestHeaders(request.headers().asHttpHeaders().toSingleValueMap())
            .pathVariables(request.pathVariables())
            .requestParams(request.queryParams().toSingleValueMap());

        return extractBody(request)
            .map(body -> {
                requestResponseLogger.logRequestBody(body);
                return requestContextBuilder.requestBody(body).build();
            })
            .switchIfEmpty(Mono.just(requestContextBuilder.build()))
            .flatMap(requestContext ->
                route.findRule(RequestContextMap.of(requestContext))
                    .map(rule -> render(rule.getResponse(), requestContext))
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

    public Mono<ServerResponse> render(CompiledResponse compiledResponse, RequestContext requestContext) {
        return renderHeadersAndBody(compiledResponse, requestContext)
            .delayElement(Duration.ofMillis(compiledResponse.getDelay()));
    }

    protected Mono<ServerResponse> renderHeadersAndBody(CompiledResponse compiledResponse, RequestContext requestContext) {
        val httpStatus = compiledResponse.getHttpStatus();
        val responseContext = ResponseContext.of(compiledResponse, requestContext);
        val responseContextMap = ResponseContextMap.of(responseContext);
        val headers = compiledResponse.renderHeaders(responseContextMap);

        val builder = ServerResponse.status(httpStatus);
        headers.forEach(builder::header);
        requestResponseLogger.logResponseHeaders(httpStatus, headers);

        val renderedBody = compiledResponse.renderBody(responseContextMap);
        if (renderedBody == null) {
            return builder.build();
        } else {
            requestResponseLogger.logResponseBody(renderedBody);
            return builder.syncBody(renderedBody);
        }
    }

    private Mono<String> extractBodyAsString(ServerRequest request) {
        return request.bodyToMono(String.class);
    }

    private Mono<Map> extractBodyAsXml(ServerRequest request) {
        return extractBodyAsString(request).map(body -> {
            try {
                return xmlMapper.readValue(body, Map.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Mono<Void> extractBodyAsBytes(ServerRequest request) {
        // TODO: provide bytes to script
        return Mono.empty();
    }
}
