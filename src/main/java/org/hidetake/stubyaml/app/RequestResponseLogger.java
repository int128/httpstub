package org.hidetake.stubyaml.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

@Slf4j
@Component
public class RequestResponseLogger {
    public void logRequestHeaders(ServerRequest request) {
        log.info("->| {}", request);
        request.headers().asHttpHeaders().forEach((key, values) ->
            values.forEach(value -> log.info("->| {}: {}", key, value)));
        log.info("->|");
    }

    public void logRequestBody(Object body) {
        if (body != null) {
            log.info("->| {}", body);
        }
    }

    public void logResponseHeaders(HttpStatus status, Map<String, String> headers) {
        log.info("<-| {} {}", status.value(), status.getReasonPhrase());
        headers.forEach((key, value) -> log.info("<-| {}: {}", key, value));
        log.info("<-|");
    }

    public void logResponseBody(Object body) {
        if (body != null) {
            log.info("<-| {}", body);
        }
    }
}
