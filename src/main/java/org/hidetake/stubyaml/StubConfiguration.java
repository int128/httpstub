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
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class StubConfiguration {
    @Getter
    @Setter
    private String path = "data";

    private final RuleYamlLoader ruleYamlLoader;
    private final RouteCompiler routeCompiler;

    @Bean
    RequestMappingHandlerMapping stubRequestHandlerMapping() throws NoSuchMethodException {
        val mapping = new RequestMappingHandlerMapping();
        mapping.setOrder(Integer.MAX_VALUE - 2);

        val handle = StubRequestController.class.getMethod("handle", Map.class, Map.class, Map.class);

        ruleYamlLoader.walk(new File(path))
            .map(routeCompiler::compile)
            .forEach(route ->
                mapping.registerMapping(
                    route.getRequestMappingInfo(),
                    new StubRequestController(route),
                    handle));

        return mapping;
    }
}
