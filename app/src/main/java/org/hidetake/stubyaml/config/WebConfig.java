package org.hidetake.stubyaml.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.yaml.snakeyaml.Yaml;

@Order
@Configuration
public class WebConfig {

    @Bean
    @ConditionalOnMissingBean
    public Yaml appYamlMapper() {
        return new Yaml();
    }

}
