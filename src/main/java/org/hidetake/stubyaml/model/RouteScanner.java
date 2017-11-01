package org.hidetake.stubyaml.model;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.hidetake.stubyaml.model.yaml.Route;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor
@Component
public class RouteScanner {
    private static final Pattern PATH_PATTERN = Pattern.compile("(.+)\\.(.+?)\\.(.+?)$");

    private final RuleParser ruleParser;

    public Stream<Route> scan(File baseDirectory) throws IOException {
        log.info("Scanning files in {}", baseDirectory.getAbsolutePath());
        val basePath = baseDirectory.toPath();
        return Files.walk(basePath)
            .filter(path -> path.toFile().isFile())
            .flatMap(path -> mapToRoute(toUrlPath(basePath.relativize(path)), path.toFile()));
    }

    private Stream<Route> mapToRoute(String path, File file) {
        val m = PATH_PATTERN.matcher(path);
        if (m.matches()) {
            val requestPath = m.group(1);
            val requestMethodString = m.group(2).toUpperCase();
            val extension = m.group(3);

            if (Objects.equals(extension, "yaml")) {
                log.info("Loading {}", path);
                return requestMethodOf(requestMethodString)
                    .map(requestMethod ->
                        Stream.of(new Route(requestPath, requestMethod, ruleParser.parse(file))))
                    .orElseGet(() -> {
                        log.error("Ignored invalid request method {} for path {}", requestMethodString, path);
                        return Stream.empty();
                    });
            } else {
                log.warn("Ignored file {}", path);
                return Stream.empty();
            }
        } else {
            log.warn("Ignored file {}", path);
            return Stream.empty();
        }
    }

    private static String toUrlPath(Path path) {
        return "/" + StreamSupport.stream(path.spliterator(), false)
            .map(Path::toString)
            .collect(Collectors.joining("/"));
    }

    private static Optional<RequestMethod> requestMethodOf(String value) {
        try {
            return Optional.of(RequestMethod.valueOf(value));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
