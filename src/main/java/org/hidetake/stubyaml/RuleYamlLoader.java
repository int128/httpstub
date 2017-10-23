package org.hidetake.stubyaml;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.hidetake.stubyaml.model.Rule;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
@Component
public class RuleYamlLoader {
    private static final Pattern PATH_PATTERN = Pattern.compile("(.+)\\.(.+?)\\.(.+?)$");

    public Stream<Rule> walk(File baseDirectory) {
        if (!baseDirectory.isDirectory()) {
            throw new IllegalStateException("Directory did not found: " + baseDirectory);
        }
        val basePath = baseDirectory.toPath();
        try {
            return Files.walk(basePath)
                    .filter(path -> path.toFile().isFile())
                    .map(path -> mapToRule(basePath.relativize(path), path.toFile()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Rule mapToRule(Path path, File file) {
        val m = PATH_PATTERN.matcher(path.toString());
        if (m.matches()) {
            val realPath = "/" + m.group(1);
            val method = m.group(2).toUpperCase();
            val extension = m.group(3);
            return new Rule(realPath, RequestMethod.valueOf(method), file);
        } else {
            log.info("Ignored file {}", path);
            return null;
        }
    }
}
