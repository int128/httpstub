package org.hidetake.stubyaml.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@Component
@Order(-2)
public class RestExceptionHandler extends AbstractErrorWebExceptionHandler {

    public RestExceptionHandler(RestErrorAttributes attributes,
                                ApplicationContext applicationContext,
                                ServerCodecConfigurer serverCodecConfigurer) {
        super(attributes, new ResourceProperties(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(
            RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(
        ServerRequest request) {
        Map<String, Object> errorPropertiesMap = getErrorAttributes(request, false);
        MediaType mediaType = request.headers().contentType().orElse(MediaType.APPLICATION_JSON_UTF8);
        Integer status = Integer.valueOf(errorPropertiesMap.getOrDefault("status", INTERNAL_SERVER_ERROR.value()) + "");

        return ServerResponse.status(status)
            .contentType(mediaType)
            .body(BodyInserters.fromValue(errorPropertiesMap));
    }

}
