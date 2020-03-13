package org.hidetake.stubyaml.model.yaml;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Data
@Slf4j
public class FilenameRouteSource implements RouteSource {

    private static final Pattern PATH_PATTERN = Pattern.compile("(.+)\\.(.+?)\\.(.+?)$");

    private final File file;


    public Route parse(String relativePath) {
        final var matcher = PATH_PATTERN.matcher(relativePath);
        if (matcher.matches()) {
            final var type = Route.RouteType.valueOf(matcher.group(3).toUpperCase());
            String path = matcher.group(1);
            String method = matcher.group(2);

            return Route.builder()
                .requestPath(path)
                .method(method)
                .type(type)
                .build();
        }

        return null;
    }

    public String computeRelativePath(File file, Path basePath) {
        final var relativePath = basePath.relativize(file.toPath());
        return "/" + StreamSupport.stream(relativePath.spliterator(), false)
            .map(Path::toString)
            .collect(Collectors.joining("/"));
    }

}
