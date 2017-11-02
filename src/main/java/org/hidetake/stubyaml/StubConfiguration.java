package org.hidetake.stubyaml;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.File;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class StubConfiguration {
    @Getter @Setter
    private String dataDirectory = "data";

    private final RouteRegistrar routeRegistrar;
    private final RouteWatcher routeWatcher;

    @Bean
    RequestMappingHandlerMapping stubRequestHandlerMapping() throws IOException, InterruptedException {
        val mapping = new RequestMappingHandlerMapping();
        mapping.setOrder(Integer.MAX_VALUE - 2);

        val baseDirectory = new File(dataDirectory);
        routeRegistrar.register(mapping, baseDirectory);
        routeWatcher.runOnFileChangeContinuously(baseDirectory, () -> {
            log.info("Reload routes after 2 seconds...");
            Thread.sleep(2000L);

            log.info("Reloading routes...");
            routeRegistrar.unregister(mapping);
            routeRegistrar.register(mapping, baseDirectory);
            return null;
        });

        return mapping;
    }
}
