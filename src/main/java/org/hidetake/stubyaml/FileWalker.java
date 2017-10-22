package org.hidetake.stubyaml;

import lombok.RequiredArgsConstructor;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class FileWalker {
    private final String path;

    public Stream<File> walk() {
        val directory = new File(path);
        if (!directory.isDirectory()) {
            throw new IllegalStateException("Data directory does not exist: " + path);
        }
        try {
            return Files.walk(directory.toPath()).map(Path::toFile).filter(File::isFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
