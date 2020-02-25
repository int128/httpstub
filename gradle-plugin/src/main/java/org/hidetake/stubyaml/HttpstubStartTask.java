package org.hidetake.stubyaml;

import lombok.Data;
import lombok.SneakyThrows;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Lists.newArrayList;

@Data
public class HttpstubStartTask extends AbstractTask {

    @TaskAction
    @SneakyThrows
    public void launch() {
        var extension = getProject().getExtensions().getByType(HttpstubExtension.class);

        if (!isPortAvailable(extension.getServerPort())) {
            log.warn("Httpstub is already running!");
            return;
        }

        ClasspathFinder classpathFinder = new ClasspathFinder(getProject());
        start(classpathFinder, extension);
    }

    @SneakyThrows
    private void start(ClasspathFinder classpathFinder, HttpstubExtension extension) {
        var file = classpathFinder.find();
        var processBuilder = new ProcessBuilder();

        log.info("Starting httpstub, path: {}", file.getAbsolutePath());
        List<String> commandArgs = newArrayList("java", "-jar", file.getAbsolutePath());
        List<String> userSettings = extension.toArgsList();
        commandArgs.addAll(userSettings);

        String[] args = commandArgs.toArray(new String[userSettings.size()]);
        processBuilder.command(args);
        var process = processBuilder.start();

        Thread stdOut = new Thread(new Sink(process.getInputStream(), log, (line, sink) -> {
            log.info(line);
            if (line.contains(extension.getAwaitedLine())) {
                log.info("Httpstub is ready");
                sink.ready();
            }
        }));

        var name = Thread.currentThread().getName();
        stdOut.setDaemon(true);
        stdOut.setName(name + "-sink");
        stdOut.start();

        int exitStatus = readExitStatus(process, extension.getTaskTimeout());

        if (exitStatus == -1) {
            log.warn("Command starting more than {} seconds, tired of waiting", extension.getTaskTimeout());
        } else if (exitStatus > 0) {
            throw new GradleException("Httpstub launch failed, please re-run command with --info --stacktrace option to get more log output");
        }
    }

    private int readExitStatus(Process process, long taskTimeout) {
        var future = CompletableFuture.supplyAsync(() ->
            readExitValue(process));
        int exitValue;

        try {
            exitValue = future.get(taskTimeout, TimeUnit.SECONDS);
        } catch (Exception ignored) {
            exitValue = -1;
        }

        return exitValue;
    }

    private int readExitValue(Process process) {
        int exitValue;

        try {
            process.waitFor();
            exitValue = process.exitValue();
        } catch (Exception ignored) {
            exitValue = -1;
        }

        return exitValue;
    }

}
