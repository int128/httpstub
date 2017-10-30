package org.hidetake.stubyaml.model;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.hidetake.stubyaml.model.yaml.Route;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Component
public class RuleScanner {
    private static final Pattern PATH_PATTERN = Pattern.compile("(.+)\\.(.+?)\\.(.+?)$");
    private static final Pattern PATH_VARIABLE_PATTERN = Pattern.compile("_(.+?)_");

    private final RuleParser ruleParser;

    public Stream<Route> scan(File baseDirectory) {
        if (!baseDirectory.isDirectory()) {
            throw new IllegalStateException("Directory did not found: " + baseDirectory);
        }
        log.info("Scanning {}", baseDirectory);
        val basePath = baseDirectory.toPath();
        try {
            return Files.walk(basePath)
                .filter(path -> path.toFile().isFile())
                .flatMap(path -> mapToRule(basePath.relativize(path), path.toFile()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Stream<Route> mapToRule(Path path, File file) {
        val m = PATH_PATTERN.matcher(path.toString());
        if (m.matches()) {
            val requestPath = replacePathVariables("/" + m.group(1));
            val requestMethodString = m.group(2).toUpperCase();
            val extension = m.group(3);

            if ("yaml".equals(extension)) {
                log.info("Loading /{}", path);
                return requestMethodOf(requestMethodString)
                    .map(requestMethod -> {
                        try {
                            val route = ruleParser.parse(file, requestPath, requestMethod);
                            return Stream.of(route);
                        } catch (YAMLException e) {
                            log.error("Ignored invalid YAML file /{}\n{}", path, e.getLocalizedMessage());
                            return Stream.<Route>empty();
                        }
                    })
                    .orElseGet(() -> {
                        log.error("Ignored invalid request method {} of file /{}", requestMethodString, path);
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

    private static String replacePathVariables(String path) {
        return PATH_VARIABLE_PATTERN.matcher(path).replaceAll("{$1}");
    }

    private static Optional<RequestMethod> requestMethodOf(String value) {
        try {
            return Optional.of(RequestMethod.valueOf(value));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
