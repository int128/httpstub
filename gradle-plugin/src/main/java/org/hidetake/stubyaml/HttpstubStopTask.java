package org.hidetake.stubyaml;

import lombok.SneakyThrows;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpstubStopTask extends DefaultAppTask {

    @Input
    public String adminPrefix = "/admin";

    @TaskAction
    public void stop() {
        if(!isPortAvailable(appPort)) {
            log.warn("Httpstub doesn't work");
            return;
        }

        stopApplication(appPort);
    }

    @SneakyThrows
    private void stopApplication(String port) {
        String shutdownUrl = String.format("http://127.0.0.1:%s%s/shutdown", port, adminPrefix);

        sendGet(shutdownUrl);
    }

    private void sendGet(String url) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();

        HttpRequest request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(url))
            .build();

        httpClient.send(request, HttpResponse.BodyHandlers.discarding());
    }

}
