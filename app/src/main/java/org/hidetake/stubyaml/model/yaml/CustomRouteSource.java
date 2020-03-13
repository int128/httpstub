package org.hidetake.stubyaml.model.yaml;

import lombok.Builder;
import lombok.Value;

import java.io.File;
import java.nio.file.Path;

@Value
@Builder
public class CustomRouteSource implements RouteSource {

    private final FilenameRouteSource source;
    private final boolean relative;
    private final String path;
    private final String method;
    private final Route.RouteType type;


    @Override
    public File getFile() {
        return source.getFile();
    }

    @Override
    public Route parse(String relativePath) {
        return Route.builder()
            .requestPath(join(relativePath, path))
            .method(method)
            .type(type)
            .build();
    }

    @Override
    public String computeRelativePath(File file, Path basePath) {
        return relative ? source.computeRelativePath(file.getParentFile(), basePath) : "";
    }

    public String join(String basePath, String path) {
        return (basePath + "/" + path).replace("//", "/");
    }

}
