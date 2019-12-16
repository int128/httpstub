package org.hidetake.stubyaml.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.hidetake.stubyaml.model.execution.CompiledResponse;
import org.hidetake.stubyaml.model.execution.RequestContext;
import org.hidetake.stubyaml.model.execution.ResponseContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_XML;

@Component
@RequiredArgsConstructor
public class ResponseRenderer {

    private final RequestResponseLogger requestResponseLogger;
    private final ObjectMapper objectMapper;
    private final XmlMapper xmlMapper = new XmlMapper();

    public Mono<ServerResponse> render(CompiledResponse compiledResponse, RequestContext requestContext) {
        final var responseContext = ResponseContext.builder()
            .requestContext(requestContext)
            .resolvedTable(compiledResponse.getTables().resolve(requestContext))
            .build();
        return renderInternal(compiledResponse, responseContext)
            .delayElement(compiledResponse.getDelay());
    }

    private Mono<ServerResponse> renderInternal(CompiledResponse compiledResponse, ResponseContext responseContext) {
        final var headers = new HttpHeaders();
        headers.addAll(compiledResponse.evaluateHeaders(responseContext));

        final var responseBuilder = ServerResponse
            .status(compiledResponse.getHttpStatus())
            .headers(httpHeaders -> httpHeaders.putAll(headers));

        final var evaluatedBody = compiledResponse.getBody().evaluate(responseContext);
        if (evaluatedBody == null) {
            return responseBuilder.build()
                .doOnSuccess(response -> requestResponseLogger.logResponse(response, null));
        } else if (evaluatedBody instanceof File) {
            final var resource = new FileSystemResource((File) evaluatedBody);
            return responseBuilder
                .body(BodyInserters.fromResource(resource))
                .doOnSuccess(response -> requestResponseLogger.logResponse(response, resource.toString()));
        } else {
            final var serializedBody = serialize(evaluatedBody, headers.getContentType());
            return responseBuilder
                .syncBody(serializedBody)
                .doOnSuccess(response -> requestResponseLogger.logResponse(response, serializedBody));
        }
    }

    private String serialize(@Nullable Object body, @Nullable MediaType contentType) {
        if (body == null) {
            return null;
        } else if (body instanceof String) {
            return (String) body;
        } else if (APPLICATION_JSON.includes(contentType)) {
            // Convert to String in order to send non UTF-8 charset
            try {
                return objectMapper.writeValueAsString(body);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (APPLICATION_XML.includes(contentType)) {
            // Convert to String in order to send non UTF-8 charset
            try {
                return xmlMapper.writeValueAsString(body);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return body.toString();
        }
    }

}
