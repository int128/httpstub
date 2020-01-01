package org.hidetake.stubyaml;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;

public class AppPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        TaskContainer tasks = project.getTasks();
        tasks.create("httpstubStart", HttpstubStartTask.class);
        tasks.create("httpstubStop", HttpstubStopTask.class);
    }

}
