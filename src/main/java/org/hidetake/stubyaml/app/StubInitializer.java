package org.hidetake.stubyaml.app;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.time.Duration;

@RequiredArgsConstructor
@Configuration
public class StubInitializer implements InitializingBean {
    private final DirectoryWatcher directoryWatcher;
    private final RouteRegistrar routeRegistrar;

    @Getter @Setter
    private String data = "data";

    @Override
    public void afterPropertiesSet() {
        val baseDirectory = new File(data);
        directoryWatcher.startThread(
            baseDirectory,
            Duration.ofSeconds(3),
            () -> routeRegistrar.register(baseDirectory));
    }
}
