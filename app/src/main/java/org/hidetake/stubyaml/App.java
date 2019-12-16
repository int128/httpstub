package org.hidetake.stubyaml;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class App implements Application {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}
