package org.hidetake.stubyaml.app;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.hidetake.stubyaml.model.execution.RequestContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Map;

import static org.springframework.http.MediaType.*;

@Component
public class RequestExtractor {
    private static final ParameterizedTypeReference<Map<String, Object>> MAP_TYPE =
        new ParameterizedTypeReference<Map<String, Object>>() {};
    private static final MediaType TEXT_ALL = MediaType.valueOf("text/*");

    private final XmlMapper xmlMapper = new XmlMapper();

    public Mono<RequestContext> extract(ServerRequest request) {
        return extractBody(request)
            .map(body ->
                RequestContext.builder()
                    .requestHeaders(request.headers().asHttpHeaders().toSingleValueMap())
                    .pathVariables(request.pathVariables())
                    .requestParams(request.queryParams().toSingleValueMap())
                    .requestBody(body)
                    .build())
            .switchIfEmpty(Mono.fromSupplier(() ->
                RequestContext.builder()
                    .requestHeaders(request.headers().asHttpHeaders().toSingleValueMap())
                    .pathVariables(request.pathVariables())
                    .requestParams(request.queryParams().toSingleValueMap())
                    .build()));
    }

    private Mono<?> extractBody(ServerRequest request) {
        return request.headers().contentType().map(contentType -> {
            if (APPLICATION_FORM_URLENCODED.includes(contentType)) {
                return request.body(BodyExtractors.toFormData()).map(MultiValueMap::toSingleValueMap);
            } else if (MULTIPART_FORM_DATA.includes(contentType)) {
                return request.body(BodyExtractors.toMultipartData()).map(MultiValueMap::toSingleValueMap);
            } else if (APPLICATION_JSON.includes(contentType)) {
                return request.bodyToMono(MAP_TYPE);
            } else if (APPLICATION_XML.includes(contentType)) {
                return extractBodyAsXml(request);
            } else if (TEXT_ALL.includes(contentType)) {
                return extractBodyAsString(request);
            } else {
                return extractBodyAsBytes(request);
            }
        }).orElseGet(() -> extractBodyAsBytes(request));
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
