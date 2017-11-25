package org.hidetake.stubyaml;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.io.File;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class StubConfiguration {
    @Getter @Setter
    private String dataDirectory = "data";

    private final RouteRegistrar routeRegistrar;
    private final RouteWatcher routeWatcher;

    @Bean
    RouterFunction<ServerResponse> routes() {
        val baseDirectory = new File(dataDirectory);
        return routeRegistrar.register(baseDirectory);
    }

//    @Bean
//    RequestMappingHandlerMapping stubRequestHandlerMapping() throws IOException, InterruptedException {
//        val mapping = new RequestMappingHandlerMapping();
//        mapping.setOrder(Integer.MAX_VALUE - 2);
//
//        val baseDirectory = new File(dataDirectory);
//        routeRegistrar.register(mapping, baseDirectory);
//        routeWatcher.runOnFileChangeContinuously(baseDirectory, () -> {
//            log.info("Reload routes after 2 seconds...");
//            Thread.sleep(2000L);
//
//            log.info("Reloading routes...");
//            routeRegistrar.unregister(mapping);
//            routeRegistrar.register(mapping, baseDirectory);
//            return null;
//        });
//
//        return mapping;
//    }
}
