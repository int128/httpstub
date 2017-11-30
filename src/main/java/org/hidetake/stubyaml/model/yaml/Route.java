package org.hidetake.stubyaml.model.yaml;

import lombok.Data;

@Data
public class Route {
    private final String requestPath;
    private final String method;
    private final RouteType type;

    public enum RouteType {
        YAML
    }
}
