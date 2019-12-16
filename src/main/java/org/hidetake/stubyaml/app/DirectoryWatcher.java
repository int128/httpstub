package org.hidetake.stubyaml.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.WatchService;
import java.time.Duration;

import static java.nio.file.StandardWatchEventKinds.*;

@Slf4j
@Component
public class DirectoryWatcher {

    /**
     * Run {@link #watch(File, Duration, Runnable)} in another thread.
     *
     * @param baseDirectory
     * @param wait
     * @param handler
     */
    @Async
    public void startThread(File baseDirectory, Duration wait, Runnable handler) {
        watch(baseDirectory, wait, handler);
    }

    /**
     * Run the handler, wait for file change and interval.
     * This function exits if {@link InterruptedException} is occurred.
     *
     * @param baseDirectory
     * @param interval
     * @param handler
     */
    public void watch(File baseDirectory, Duration interval, Runnable handler) {
        while (true) {
            try {
                handler.run();
                try (final var watchService = FileSystems.getDefault().newWatchService()) {
                    register(watchService, baseDirectory);
                    log.info("Waiting for change of file or directory in {}", baseDirectory.getAbsolutePath());
                    waitForFileChange(watchService);
                    log.info("File or directory has been changed, waiting {} sec...", interval.getSeconds());
                    Thread.sleep(interval.toMillis());
                } catch (InterruptedException e) {
                    log.info("Thread has been interrupted: {}", e.toString());
                    return;
                }
            } catch (Exception e1) {
                log.warn("Error occurred while watching directory, retrying...", e1);
                try {
                    Thread.sleep(interval.toMillis());
                } catch (InterruptedException e2) {
                    log.info("Thread has been interrupted: {}", e2.toString());
                    return;
                }
            }
        }
    }

    private void register(WatchService watchService, File baseDirectory) throws IOException {
        Files.walk(baseDirectory.toPath())
            .filter(path -> path.toFile().isDirectory())
            .forEach(path -> {
                try {
                    path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
                } catch (IOException e) {
                    log.warn("Ignored directory {}", path.toAbsolutePath(), e);
                }
            });
    }

    private void waitForFileChange(WatchService watchService) throws InterruptedException {
        while (true) {
            final var watchKey = watchService.take();
            if (watchKey.pollEvents().stream().anyMatch(event -> event.kind() != OVERFLOW)) {
                break;
            }
        }
    }

}
