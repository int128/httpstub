package org.hidetake.stubyaml;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import java.io.File;
import java.util.HashMap;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class StubConfiguration {
    @Getter
    @Setter
    private String path = "data";

    private final RuleYamlProcessor ruleYamlProcessor;

    @Bean
    SimpleUrlHandlerMapping stubRequestHandlerMapping() {
        val map = new HashMap<String, Object>();
        ruleYamlProcessor.walk(new File(path)).forEach(rule -> {
            log.debug("rule={}", rule);
        });

        val mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Integer.MAX_VALUE - 2);
        mapping.setUrlMap(map);
        return mapping;
    }
}
