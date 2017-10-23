package org.hidetake.stubyaml;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.File;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class StubConfiguration {
    @Getter
    @Setter
    private String path = "data";

    private final RuleYamlLoader ruleYamlLoader;

    @Bean
    RequestMappingHandlerMapping stubRequestHandlerMapping() throws NoSuchMethodException {
        val mapping = new RequestMappingHandlerMapping();
        mapping.setOrder(Integer.MAX_VALUE - 2);

        val method = StubRequestController.class.getMethod("handle", MultiValueMap.class, Map.class);

        ruleYamlLoader.walk(new File(path))
            .forEach(rule -> {
                mapping.registerMapping(rule.getRequestMappingInfo(), new StubRequestController(rule), method);
            });

        return mapping;
    }
}
