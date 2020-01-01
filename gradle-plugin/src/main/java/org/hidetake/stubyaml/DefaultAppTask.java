package org.hidetake.stubyaml;

import lombok.Data;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;

import java.io.File;

@Data
public class DefaultAppTask extends DefaultTask {

    @Internal
    protected final Logger log;

    @Input
    public String lockFileName = ".httpstub.lock";
    @Input
    public String adminPrefix = "/admin";

    public DefaultAppTask() {
        super();
        setGroup("httpstub");
        setDescription("Declarative YAML based HTTP stub server for integration tests such as enterprise external APIs");
        log = super.getLogger();
    }

    @Internal
    protected File getLockFile() {
        return new File(lockFileName);
    }

}
