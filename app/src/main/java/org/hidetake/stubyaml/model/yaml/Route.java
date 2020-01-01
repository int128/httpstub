package org.hidetake.stubyaml.model.yaml;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Route {

    private final String requestPath;
    private final String method;
    private final RouteType type;

    public enum RouteType {

        YAML,
        YML

    }

}
