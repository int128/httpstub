package org.hidetake.stubyaml.app;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.Part;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
@Component
public class RequestResponseLogger {
    private static final String RECEIVED = ">";
    private static final String SENT = "<";

    private final ConfigHolder configHolder;

    public void logRequest(ServerRequest request, @Nullable MultiValueMap<String, ?> map) {
        if (configHolder.getConfig().getLogging().isHeaders()) {
            logRequestHeaders(request);
        }
        if (configHolder.getConfig().getLogging().isBody()) {
            if (map != null) {
                map.forEach((key, values) -> values.forEach(value -> {
                    if (value instanceof Part) {
                        final var part = (Part) value;
                        part.headers().forEach((headerKey, headerValues) -> headerValues.forEach(headerValue ->
                            log.info("{} [{}] {}: {}", RECEIVED, key, headerKey, headerValue)));
                    } else {
                        log.info("{} {}={}", RECEIVED, key, value);
                    }
                }));
            }
        }
    }

    public void logRequest(ServerRequest request, @Nullable String body) {
        if (configHolder.getConfig().getLogging().isHeaders()) {
            logRequestHeaders(request);
        }
        if (configHolder.getConfig().getLogging().isBody()) {
            if (body != null) {
                Arrays.stream(body.split("\r\n|\r|\n")).forEach(line ->
                    log.info("{} {}", RECEIVED, line));
            }
        }
    }

    private void logRequestHeaders(ServerRequest request) {
        log.info("{} {}", RECEIVED, request);
        request.headers().asHttpHeaders().forEach((key, values) ->
            values.forEach(value -> log.info("{} {}: {}", RECEIVED, key, value)));
        log.info(RECEIVED);
    }

    public void logResponse(ServerResponse response, @Nullable String body) {
        if (configHolder.getConfig().getLogging().isHeaders()) {
            final var status = response.statusCode();
            log.info("{} {} {}", SENT, status.value(), status.getReasonPhrase());
            response.headers().forEach((key, values) ->
                values.forEach(value -> log.info("{} {}: {}", SENT, key, value)));
            log.info(SENT);
        }
        if (configHolder.getConfig().getLogging().isBody()) {
            if (body != null) {
                Arrays.stream(body.split("\r\n|\r|\n")).forEach(line ->
                    log.info("{} {}", SENT, line));
            }
        }
    }
}
