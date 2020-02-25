package org.hidetake.stubyaml.controller;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.system.ApplicationPid;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
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

    private void killApplication() {
        log.info("Killing current application");
        try {
            TimeUnit.SECONDS.sleep(3);
            String currentPid = new ApplicationPid().toString();
            boolean isWindows = System.getProperty("os.name").toLowerCase().indexOf("windows") > -1;
            if(!"???".equals(currentPid)) {
                kill(currentPid, isWindows);
            } else {
                log.info("Couldn't find current pid");
            }
        } catch (Throwable e) {
            log.error("ERROR: ", e);
        }
    }

    private void kill(String currentPid, boolean isWindows) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process process;
        if(isWindows) {
            process = runtime.exec("taskkill /F /PID " + currentPid);
        } else {
            process = runtime.exec("kill -9 " + currentPid);
        }

        process.destroyForcibly();
    }

    private void closeContext(ApplicationContext applicationContext) {
        log.info("Closing current context");
        try {
            TimeUnit.SECONDS.sleep(1);
            ((ConfigurableApplicationContext) applicationContext).close();
        } catch (Throwable e) {
            log.error("ERROR: ", e);
        }
    }

}
