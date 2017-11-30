package org.hidetake.stubyaml.app;

import lombok.val;
import org.hidetake.stubyaml.model.execution.CompiledResponse;
import org.hidetake.stubyaml.model.execution.RequestContext;
import org.hidetake.stubyaml.model.execution.ResponseContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ResponseRenderer {
    public Mono<ServerResponse> render(CompiledResponse response, RequestContext requestContext) {
        val responseContext = ResponseContext.builder()
            .requestContext(requestContext)
            .resolvedTable(response.getTables().resolve(requestContext))
            .build();
        return ServerResponse
            .status(response.getHttpStatus())
            .headers(httpHeaders -> httpHeaders.setAll(response.evaluateHeaders(responseContext)))
            .body(response.getBody().render(responseContext))
            .delayElement(response.getDelay());
    }
}
