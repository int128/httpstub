package org.hidetake.stubyaml;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.tasks.TaskContainer;

public class HttpstubPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        TaskContainer tasks = project.getTasks();
        ExtensionContainer extensions = project.getExtensions();
        extensions.create("httpstub", HttpstubExtension.class);
        tasks.create("httpstubStart", HttpstubStartTask.class);
        tasks.create("httpstubStop", HttpstubStopTask.class);
    }

}
