package org.hidetake.stubyaml;

import lombok.SneakyThrows;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpstubStopTask extends AbstractTask {

    @TaskAction
    public void stop() {
        var extension = getProject().getExtensions().getByType(HttpstubExtension.class);
        if (isPortAvailable(extension.getServerPort())) {
            log.warn("Httpstub doesn't work on port {}", extension.getServerPort());
            return;
        }

        stopApplication(extension.getServerPort(), extension.getAdminPrefix());
    }

    @SneakyThrows
    private void stopApplication(String port, String adminPrefix) {
        String shutdownUrl = String.format("http://127.0.0.1:%s%s/shutdown", port, adminPrefix);

        try {
            log.info("Sending shutdown command to httpstub: {}", shutdownUrl);
            sendGet(shutdownUrl);
        } catch (ConnectException e) {
            log.error("Httpstub doesn't work");
        }
    }

    private void sendGet(String address) throws IOException{
        URL url = new URL(address);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");

        con.setDoOutput(true);
        con.getResponseCode();
    }

}
