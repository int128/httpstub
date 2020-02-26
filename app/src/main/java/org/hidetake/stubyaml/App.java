package org.hidetake.stubyaml;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@EnableAsync
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        try {
            SpringApplication.run(App.class, args);
        } catch (Throwable e) {
            log.error("ERROR: ", e);
            System.exit(1);
        }
    }

}
