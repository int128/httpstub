package org.hidetake.stubyaml;

import lombok.Data;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Data
public class HttpstubStartTask extends DefaultAppTask {

    @Input
    public Long taskTimeout = 10L;
    @Input
    public String appPort = "8080";


    @TaskAction
    @SneakyThrows
    public void launch() {
        final var lockFile = getLockFile();
        if (lockFile.exists()) {
            throw new GradleException("Httpstub already running!");
        }

        await();
    }

    private void await() throws InterruptedException, java.util.concurrent.ExecutionException {
        ClasspathFinder classpathFinder = new ClasspathFinder(getProject());
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
//        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> start(classpathFinder, contextClassLoader));
        start(classpathFinder, contextClassLoader);
//        try {
            appPort = "8080";
//            appPort = future.get(taskTimeout, TimeUnit.SECONDS);
            savePort(appPort);
//        } catch (TimeoutException e) {
//            log.warn("Httpstub starting more than {} seconds, tired of waiting", taskTimeout);
//            //Optimistic lock
//            savePort(appPort);
//        }
    }

    @SneakyThrows
    public void savePort(@NonNull String port) {
        Files.write(getLockFile().toPath(), port.getBytes(StandardCharsets.UTF_8));
    }

    @SneakyThrows
    public String start(ClasspathFinder classpathFinder, ClassLoader classLoader) {
        var file = classpathFinder.find();
        var latch = new CountDownLatch(1);
        var processBuilder = new ProcessBuilder();

        processBuilder.command("java", "-jar", file.getAbsolutePath());
        log.info("path: {}", file.getAbsolutePath());
        var process = processBuilder.start();

        Thread stdOut = new Thread(new Sink(process.getInputStream(), log, (line, sink) -> {
            log.info(line);
            if (line.contains("OK")) {
                log.quiet("Command $command is ready");
                sink.ready();
//                    stampLockFile(pidFile, process);
                latch.countDown();
            }
        }));

        var name = Thread.currentThread().getName();

        stdOut.setDaemon(true);
        stdOut.setName(name + "-sink");

        stdOut.start();
        var runState = latch.await(5, TimeUnit.SECONDS);

        if(!runState) {
            log.warn("Command starting more than $timeout seconds, tired of waiting");
        }

//            if(!runState) {
//                log.warn("Command starting more than $timeout seconds, tired of waiting")
//                stampLockFile(pidFile, process)
//            }
//
//            System.out.println("processing ping command ...");
//            var task = new ProcessTask(process.getInputStream());
//            Future<List<String>> future = executor.submit(task);
//
//            // non-blocking, doing other tasks
//            System.out.println("doing task1 ...");
//            System.out.println("doing task2 ...");
//
//            var results = future.get(5, TimeUnit.SECONDS);
//
//            for (String res : results) {
//                System.out.println(res);
//            }


        return "8080";
    }

}
