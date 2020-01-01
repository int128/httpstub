package org.hidetake.stubyaml.controller;

import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Setter
@RestController
@RequestMapping("${stub.admin-prefix:/admin}")
public class ShutdownController implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @GetMapping("/shutdown")
    public Mono<Void> shutdown() {
        ((ConfigurableApplicationContext) applicationContext).close();
        return Mono.empty();
    }

}
