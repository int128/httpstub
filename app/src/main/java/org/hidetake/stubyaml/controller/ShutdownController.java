package org.hidetake.stubyaml.controller;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Setter
@RestController
@RequestMapping("${stub.admin-prefix:/admin}")
public class ShutdownController implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @GetMapping("/shutdown")
    public Mono<Void> shutdown() {
        log.info("Got shutdown command");
        CompletableFuture.runAsync(() -> closeContext(applicationContext))
                         .thenRunAsync(() -> killApplication());

        return Mono.empty();
    }

    private void closeContext(ApplicationContext applicationContext) {
        log.info("Closing current context");
        try {
            TimeUnit.MILLISECONDS.sleep(500);
            ((ConfigurableApplicationContext) applicationContext).close();
        } catch (Throwable e) {
            log.error("ERROR: ", e);
        }
    }

    @SneakyThrows
    private void killApplication() {
        log.info("Killing current application");
        TimeUnit.SECONDS.sleep(5);
        System.exit(0);
    }

}
