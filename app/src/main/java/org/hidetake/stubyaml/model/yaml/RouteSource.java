package org.hidetake.stubyaml.model.yaml;

import java.io.File;
import java.nio.file.Path;

public interface RouteSource {

    File getFile();

    Route parse(String relativePath);

    String computeRelativePath(File file, Path basePath);

}
