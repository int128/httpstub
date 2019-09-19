package org.hidetake.stubyaml.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.hidetake.stubyaml.model.execution.RequestContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Map;

import static org.springframework.http.MediaType.*;

@RequiredArgsConstructor
@Component
public class RequestExtractor {
    private static final MediaType TEXT_ALL = MediaType.valueOf("text/*");

    private final ConfigHolder configHolder;
    private final RequestResponseLogger requestResponseLogger;
    private final ObjectMapper objectMapper;
    private final XmlMapper xmlMapper = new XmlMapper();

    public Mono<RequestContext> extract(ServerRequest request) {
        final var builder = RequestContext.builder()
            .requestHeaders(request.headers().asHttpHeaders().toSingleValueMap())
            .pathVariables(request.pathVariables())
            .requestParams(request.queryParams().toSingleValueMap())
            .constants(configHolder.getConfig().getConstants());
        return extractBody(request)
            .map(body -> builder.requestBody(body).build())
            .switchIfEmpty(Mono.fromSupplier(builder::build));
    }

    private Mono<?> extractBody(ServerRequest request) {
        return request.headers().contentType().map(contentType -> {
            if (APPLICATION_FORM_URLENCODED.includes(contentType)) {
                return request.body(BodyExtractors.toFormData())
                    .doOnSuccess(map -> requestResponseLogger.logRequest(request, map))
                    .map(MultiValueMap::toSingleValueMap);
            } else if (MULTIPART_FORM_DATA.includes(contentType)) {
                return request.body(BodyExtractors.toMultipartData())
                    .doOnSuccess(map -> requestResponseLogger.logRequest(request, map))
                    .map(MultiValueMap::toSingleValueMap);
            } else if (APPLICATION_JSON.includes(contentType)) {
                // Convert to String in order to receive non UTF-8 charset
                return extractBodyAsString(request)
                    .doOnSuccess(string -> requestResponseLogger.logRequest(request, string))
                    .map(string -> {
                        try {
                            return objectMapper.readValue(string, Map.class);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
            } else if (APPLICATION_XML.includes(contentType) || TEXT_XML.includes(contentType)) {
                // Convert to String in order to receive non UTF-8 charset
                return extractBodyAsString(request)
                    .doOnSuccess(string -> requestResponseLogger.logRequest(request, string))
                    .map(string -> {
                        try {
                            return xmlMapper.readValue(string, Map.class);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
            } else if (TEXT_ALL.includes(contentType)) {
                return extractBodyAsString(request)
                    .doOnSuccess(string -> requestResponseLogger.logRequest(request, string));
            } else {
                return extractBodyAsBytes(request)
                    .doOnSuccess(x -> requestResponseLogger.logRequest(request, "[body]"));
            }
        }).orElseGet(() ->
            extractBodyAsBytes(request)
                .doOnSuccess(x -> requestResponseLogger.logRequest(request, (String) null)));
    }

    private Mono<String> extractBodyAsString(ServerRequest request) {
        return request.bodyToMono(String.class);
    }

    private Mono<Void> extractBodyAsBytes(ServerRequest request) {
        // TODO: provide bytes to script
        return Mono.empty();
    }
}
