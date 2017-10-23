package org.hidetake.stubyaml;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.File;

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

        val method = StubRequestController.class.getMethod("handle");

        ruleYamlLoader.walk(new File(path)).forEach(rule -> {
            mapping.registerMapping(
                    new RequestMappingInfo(
                            new PatternsRequestCondition(rule.getPath()),
                            new RequestMethodsRequestCondition(rule.getMethod()),
                            null,
                            null,
                            null,
                            null,
                            null
                    ),
                    new StubRequestController(rule),
                    method
            );
        });

        return mapping;
    }
}
