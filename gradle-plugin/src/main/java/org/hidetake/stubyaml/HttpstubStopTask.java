package org.hidetake.stubyaml;

import com.google.common.base.Strings;
import lombok.SneakyThrows;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpstubStopTask extends DefaultAppTask {

    @TaskAction
    public void stop() {
        final var lockFile = getLockFile();
        if(!lockFile.exists()) {
            log.warn("Httpstub does not work");
            return;
        }

        final var port = loadPort();

        try {
            if(!Strings.isNullOrEmpty(port)) {
                stopApplication(port);
            }
        } finally {
            lockFile.delete();
        }
    }

    @SneakyThrows
    private void stopApplication(String port) {
        String shutDownUrl = String.format("http://127.0.0.1:%s%s/shutdown", port, adminPrefix);

        HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();

        HttpRequest request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(shutDownUrl))
            .build();

        httpClient.send(request, HttpResponse.BodyHandlers.discarding());
    }

    @SneakyThrows
    public String loadPort() {
        File lockFile = getLockFile();
        if(lockFile.exists()) {
//            byte[] bytes = Files.readAllBytes(lockFile.toPath());
//            return new String(bytes, StandardCharsets.UTF_8);
            return "8080";
        }

        return "";
    }

}
