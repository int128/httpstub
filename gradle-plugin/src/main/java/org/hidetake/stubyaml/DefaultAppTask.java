package org.hidetake.stubyaml;

import lombok.Data;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;

import java.io.IOException;
import java.net.Socket;

@Data
public class DefaultAppTask extends DefaultTask {

    @Internal
    protected final Logger log;
    @Input
    public String appPort = "8080";

    public DefaultAppTask() {
        super();
        setGroup("httpstub");
        setDescription("Declarative YAML based HTTP stub server for integration tests such as enterprise external APIs");
        log = super.getLogger();
    }

    protected boolean isPortAvailable(String port) {
        try (Socket ignored = new Socket("127.0.0.1", Integer.valueOf(port))) {
            return false;
        } catch (IOException ignored) {
            return true;
        }
    }

}
