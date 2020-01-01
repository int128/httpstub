package org.hidetake.stubyaml.model.yaml;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

// TODO: add alternative way to register route 'name'
@Data
@Slf4j
public class FilenameRouteSource implements RouteSource {

    private static final Pattern PATH_PATTERN = Pattern.compile("(.+)\\.(.+?)\\.(.+?)$");

    private final File file;

    @Override
    public String getName() {
        return file.getPath();
    }

    @Override
    public Optional<Route> parseName(Path basePath) {
        final var relativePath = computeRelativePath(basePath);
        final var m = PATH_PATTERN.matcher(relativePath);
        if (m.matches()) {
            try {
                final var type = Route.RouteType.valueOf(m.group(3).toUpperCase());
                String path = m.group(1);
                String method = m.group(2);

                return Optional.of(Route.builder()
                    .requestPath(path)
                    .method(method)
                    .type(type)
                    .build());
            } catch (IllegalArgumentException e) {
                log.warn("Ignored route source {}, message {}", this, e.getMessage());
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    private String computeRelativePath(Path basePath) {
        final var relativePath = basePath.relativize(file.toPath());
        return "/" + StreamSupport.stream(relativePath.spliterator(), false)
            .map(Path::toString)
            .collect(Collectors.joining("/"));
    }

}
