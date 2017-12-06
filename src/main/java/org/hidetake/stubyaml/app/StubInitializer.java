package org.hidetake.stubyaml.app;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.io.File;
import java.time.Duration;

@ConfigurationProperties("stub")
@RequiredArgsConstructor
@Configuration
public class StubInitializer implements InitializingBean {
    private final DirectoryWatcher directoryWatcher;
    private final ConfigRegistrar configRegistrar;
    private final RouteRegistrar routeRegistrar;

    @Getter @Setter
    private String data = "data";

    @Getter @Setter
    private long intervalSec = 3;

    @Override
    public void afterPropertiesSet() {
        Assert.hasText(data, "stub.data must have text");
        Assert.state(intervalSec >= 0, "stub.intervalSec must be 0 or greater");

        val baseDirectory = new File(data);
        directoryWatcher.startThread(
            baseDirectory,
            Duration.ofSeconds(intervalSec),
            () -> {
                configRegistrar.register(baseDirectory);
                routeRegistrar.register(baseDirectory);
            });
    }
}
