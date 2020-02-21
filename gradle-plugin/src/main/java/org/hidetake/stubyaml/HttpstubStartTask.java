package org.hidetake.stubyaml;

import lombok.Data;
import lombok.SneakyThrows;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Data
public class HttpstubStartTask extends DefaultAppTask {

    @Input
    public Long taskTimeout = 10L;
    @Input
    private String awaitedLine = "OK";


    @TaskAction
    @SneakyThrows
    public void launch() {
        if(!isPortAvailable(appPort)) {
            log.warn("Httpstub is already running!");
            return;
        }

        ClasspathFinder classpathFinder = new ClasspathFinder(getProject());
        start(classpathFinder);
    }

    @SneakyThrows
    private void start(ClasspathFinder classpathFinder) {
        var file = classpathFinder.find();
        var latch = new CountDownLatch(1);
        var processBuilder = new ProcessBuilder();

        log.info("Starting httpstub.jar, path: {}", file.getAbsolutePath());
        processBuilder.command("java", "-jar", file.getAbsolutePath());
        var process = processBuilder.start();

        Thread stdOut = new Thread(new Sink(process.getInputStream(), log, (line, sink) -> {
            log.info(line);
            if (line.contains(awaitedLine)) {
                log.quiet("Httpstub is ready");
                sink.ready();
                latch.countDown();
            }
        }));

        var name = Thread.currentThread().getName();
        stdOut.setDaemon(true);
        stdOut.setName(name + "-sink");
        stdOut.start();

        var runState = latch.await(taskTimeout, TimeUnit.SECONDS);

        if(!runState) {
            log.warn("Command starting more than {} seconds, tired of waiting", taskTimeout);
        }
    }

}
