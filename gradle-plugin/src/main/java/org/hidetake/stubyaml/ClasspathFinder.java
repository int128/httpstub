package org.hidetake.stubyaml;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.ResolvedConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ClasspathFinder {

    public static final String JAR_PACKAGE = "org/hidetake/stubyaml";
    public static final String JAR_NAME = "app";
    public static final String JAVA_CONFIGURATION = "classpath";

    private final Project project;

    @SneakyThrows
    public File find() {
        Set<URL> classpath = Collections.emptySet();

        if(project.getPluginManager().hasPlugin("java")) {
            classpath = loadHttpstubFromBuildScript(JAVA_CONFIGURATION);
        }

        List<URL> urls = classpath.stream()
            .filter(url -> url.getFile().contains(JAR_PACKAGE))
            .filter(url -> url.getFile().contains(JAR_NAME))
            .collect(Collectors.toList());

        if(CollectionUtils.isEmpty(urls)) {
            throw new GradleException("Please add httpstub.jar into buildscript closure");
        } else if(urls.size() > 1) {
            throw new GradleException(String.format("Found to many jars in classpath, please remove unnecessary jars, jars: %s", urls));
        }

        return new File(urls.get(0).getFile());
    }

    /* ===================== */

    private Set<URL> loadHttpstubFromBuildScript(String classpath) throws IOException {
        Set<URL> urls = new HashSet<>();
        Configuration byName = project.getBuildscript().getConfigurations().getByName(classpath);
        if(Objects.nonNull(byName)) {
            ResolvedConfiguration resolvedConfiguration =
                byName.getResolvedConfiguration();

            for (ResolvedArtifact artifact : resolvedConfiguration.getResolvedArtifacts()) {
                URL artifactUrl = artifact.getFile().toURI().toURL();
                urls.add(artifactUrl);
            }
        }

        return urls;
    }

}
