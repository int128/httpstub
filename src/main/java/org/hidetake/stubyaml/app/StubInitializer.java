package org.hidetake.stubyaml.app;

import com.google.common.base.Strings;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

import javax.annotation.Nullable;
import java.io.File;
import java.time.Duration;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(StubInitializer.Config.class)
public class StubInitializer implements InitializingBean {

    private final DirectoryWatcher directoryWatcher;
    private final ConfigRegistrar configRegistrar;
    private final RouteRegistrar routeRegistrar;
    private final StubInitializer.Config config;
    private final ResourceLoader resourceLoader;


    @Override
    public void afterPropertiesSet() {
        final File baseDirectory = parseFolder();
        if(config.watcherEnabled) {
            directoryWatcher.startThread(
                baseDirectory,
                Duration.ofSeconds(config.intervalSec),
                () -> {
                    configRegistrar.register(baseDirectory);
                    routeRegistrar.register(baseDirectory);
                });
        } else {
            log.debug("To watch file changes, please enable setting stub.watcher-enabled");
        }
    }

    @Nullable
    @SneakyThrows
    private File parseFolder() {
        boolean dataIsNotEmpty = !Strings.isNullOrEmpty(config.data);
        boolean resourceIsNotEmpty = !Strings.isNullOrEmpty(config.resource);
        Assert.state(resourceIsNotEmpty && dataIsNotEmpty, "stub.data or stub.resource must have text");
        Assert.state(config.intervalSec >= 0, "stub.intervalSec must be 0 or greater");

        File file = null;
        if(dataIsNotEmpty) {
            file = new File(config.data);
        } else if(resourceIsNotEmpty) {
            Resource localResource = resourceLoader.getResource(config.resource);
            Assert.state(localResource.exists(), "Given resources doesn't exist or unreachable");
            file = localResource.getFile();
        }

        log.debug("Folder with stubs has been found at: {}", file.getAbsolutePath());

        return file;
    }

    @Data
    @ConfigurationProperties("stub")
    public static class Config {

        private String data = "data";
        private long intervalSec = 3;
        private String resource = "file:data";
        private Boolean watcherEnabled = Boolean.TRUE;

    }

}
