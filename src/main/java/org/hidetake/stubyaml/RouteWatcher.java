package org.hidetake.stubyaml;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.WatchService;
import java.util.concurrent.Callable;

import static java.nio.file.StandardWatchEventKinds.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class RouteWatcher {
    @Async
    public void runOnFileChangeContinuously(File baseDirectory, Callable callable) {
        try (val watchService = FileSystems.getDefault().newWatchService()) {
            watch(watchService, baseDirectory);
            waitForFileChange(watchService);
            callable.call();
            runOnFileChangeContinuously(baseDirectory, callable);
        } catch (Exception e) {
            log.error("Error occurred on watcher thread", e);
        }
    }

    private void watch(WatchService watchService, File baseDirectory) throws IOException {
        log.info("Initializing watcher for {}", baseDirectory.getAbsolutePath());
        Files.walk(baseDirectory.toPath())
            .filter(path -> path.toFile().isDirectory())
            .forEach(path -> {
                try {
                    path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
                    log.info("Watching directory {}", path.toAbsolutePath());
                } catch (IOException e) {
                    log.warn("Could not watch directory {}", path.toAbsolutePath(), e);
                }
            });
    }

    private void waitForFileChange(WatchService watchService) throws InterruptedException {
        log.info("Waiting for file or directory change");
        while (true) {
            val watchKey = watchService.take();
            if (watchKey.pollEvents().stream().anyMatch(event -> event.kind() != OVERFLOW)) {
                break;
            }
        }
    }
}
