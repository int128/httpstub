package org.hidetake.stubyaml;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.hidetake.stubyaml.model.RequestRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import java.util.HashMap;

@Configuration
public class StubConfiguration {
    @Getter
    @Setter
    private String path = "data";

    @Bean
    SimpleUrlHandlerMapping stubRequestHandlerMapping() {
        val map = new HashMap<String, Object>();
        new FileWalker(path).walk().forEach(file -> {
            val rule = RequestRule.fromFilename(file.getName());
            map.put(file.getName(), new StubRequestHandler(file));
        });

        val mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Integer.MAX_VALUE - 2);
        mapping.setUrlMap(map);
        return mapping;
    }
}
