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

    @Bean
    RequestMappingHandlerMapping stubRequestHandlerMapping() throws NoSuchMethodException {
        val mapping = new RequestMappingHandlerMapping();
        mapping.setOrder(Integer.MAX_VALUE - 2);

        val handle = StubRequestController.class.getMethod("handle", Map.class, Map.class, Map.class);

        ruleYamlLoader.walk(new File(path))
            .forEach(rule -> {
                mapping.registerMapping(rule.toRequestMappingInfo(), new StubRequestController(rule), handle);
            });

        return mapping;
    }
}
