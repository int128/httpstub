package org.hidetake.stubyaml.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hidetake.stubyaml.util.ExceptionUtils;
import org.hidetake.stubyaml.util.StringUtils;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Maps.newHashMap;

@Data
@Slf4j
@Component
public class RestErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        Map<String, Object> map = super.getErrorAttributes(request, includeStackTrace);
        Map<String, String> queryParams = request.queryParams().toSingleValueMap();
        Map<String, String> headers = request.headers().asHttpHeaders().toSingleValueMap();
        String method = request.methodName();
        String requestId = (String) map.get("requestId");
        Throwable throwable = getError(request);
        String body = readBody(request);

        map.put("method", method);
        map.put("headers", headers);
        map.put("queryParams", queryParams);
        map.put("body", body);

        Map<String, Object> localMap = newHashMap(map);
        localMap.put("exception", ExceptionUtils.toChain(throwable));
        String mapReport = localMap.entrySet().stream()
            .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
            .collect(Collectors.joining("\n"));

        map.values().removeIf(StringUtils::isEmpty);

        log.error("ERROR[{}]:\n{}", requestId, mapReport);

        return map;
    }

    //TODO: WTF How to take request body from webflux? It's already read. Save it somewhere and load it back here?
    private String readBody(ServerRequest request) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Flux<DataBuffer> body1 = request.exchange().getRequest().getBody();
        body1.doOnNext(dataBuffer -> {
            try {
                Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });

        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

}
