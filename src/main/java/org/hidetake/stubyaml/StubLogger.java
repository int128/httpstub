package org.hidetake.stubyaml;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.hidetake.stubyaml.model.execution.RequestContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Slf4j
@Component
public class StubLogger {
    public void logRequest(RequestContext requestContext) {
        if (log.isInfoEnabled()) {
            val request = requestContext.getRequest();
            val address = request.getRemoteAddr();
            log.info("{}> {} {}", address, request.getMethod(), request.getRequestURL());
            requestContext.getRequestHeaders().forEach((key, value) ->
                log.info("{}> {}: {}", address, key, value));
            requestContext.getPathVariables().forEach((key, value) ->
                log.info("{}> Got path variable {}={}", address, key, value));
            requestContext.getRequestParams().forEach((key, value) ->
                log.info("{}> Got request parameter {}={}", address, key, value));
            log.info("{}>", address);

            val body = requestContext.getRequestBody();
            if (body instanceof String) {
                Stream.of(body.toString().split("\r\n|\r|\n"))
                    .forEach(line -> log.info("{}> {}", address, line));
            }
            log.info("{}>", address);
        }
    }

    public void logResponse(RequestContext requestContext, ResponseEntity response) {
        if (log.isInfoEnabled()) {
            val address = requestContext.getRequest().getRemoteAddr();
            val status = response.getStatusCode();
            log.info("{}< {} {}", address, status, status.getReasonPhrase());
            response.getHeaders().toSingleValueMap().forEach((key, value) ->
                log.info("{}< {}: {}", address, key, value));
            log.info("{}<", address);

            val body = response.getBody();
            if (body instanceof String) {
                Stream.of(body.toString().split("\r\n|\r|\n"))
                    .forEach(line -> log.info("{}< {}", address, line));
            }
        }
    }
}
