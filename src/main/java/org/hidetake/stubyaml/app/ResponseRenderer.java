package org.hidetake.stubyaml.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.hidetake.stubyaml.model.execution.CompiledResponse;
import org.hidetake.stubyaml.model.execution.RequestContext;
import org.hidetake.stubyaml.model.execution.ResponseContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_XML;

@RequiredArgsConstructor
@Component
public class ResponseRenderer {
    private final ObjectMapper objectMapper;
    private final XmlMapper xmlMapper = new XmlMapper();

    public Mono<ServerResponse> render(CompiledResponse response, RequestContext requestContext) {
        val responseContext = ResponseContext.builder()
            .requestContext(requestContext)
            .resolvedTable(response.getTables().resolve(requestContext))
            .build();

        val headers = new HttpHeaders();
        headers.setAll(response.evaluateHeaders(responseContext));

        return ServerResponse
            .status(response.getHttpStatus())
            .headers(httpHeaders -> httpHeaders.putAll(headers))
            .body(renderBody(response.getBody().evaluate(responseContext), headers))
            .delayElement(response.getDelay());
    }

    private BodyInserter<?,? super ServerHttpResponse> renderBody(Object body, HttpHeaders headers) {
        if (body == null) {
            return BodyInserters.empty();
        } else if (body instanceof String) {
            return BodyInserters.fromObject(body);
        } else if (body instanceof File) {
            return BodyInserters.fromResource(new FileSystemResource((File) body));
        } else {
            val contentType = headers.getContentType();
            val serialized = serialize(body, contentType);
            return BodyInserters.fromObject(serialized);
        }
    }

    private String serialize(Object body, MediaType contentType) {
        if (APPLICATION_JSON.includes(contentType)) {
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
