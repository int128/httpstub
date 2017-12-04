package org.hidetake.stubyaml.model.yaml;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Data
public class RouteSource {
    private static final Pattern PATH_PATTERN = Pattern.compile("(.+)\\.(.+?)\\.(.+?)$");

    private final File file;

    public String getName() {
        return file.getPath();
    }

    public Optional<Route> parseName(Path basePath) {
        val relativePath = computeRelativePath(basePath);
        val m = PATH_PATTERN.matcher(relativePath);
        if (m.matches()) {
            try {
                val type = Route.RouteType.valueOf(m.group(3).toUpperCase());
                return Optional.of(new Route(m.group(1), m.group(2), type));
            } catch (IllegalArgumentException e) {
                log.warn("Ignored route source {}", this);
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    public String computeRelativePath(Path basePath) {
        val relativePath = basePath.relativize(file.toPath());
        return "/" + StreamSupport.stream(relativePath.spliterator(), false)
            .map(Path::toString)
            .collect(Collectors.joining("/"));
    }
}
