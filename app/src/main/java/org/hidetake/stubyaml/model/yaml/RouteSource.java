package org.hidetake.stubyaml.model.yaml;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public interface RouteSource {

    File getFile();

    String getName();

    Optional<Route> parseName(Path basePath);

}
