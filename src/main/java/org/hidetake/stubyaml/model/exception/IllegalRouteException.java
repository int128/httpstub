package org.hidetake.stubyaml.model.exception;

import lombok.Getter;
import org.hidetake.stubyaml.model.yaml.RouteSource;

@Getter
public class IllegalRouteException extends RuntimeException {
    private final RouteSource routeSource;

    public IllegalRouteException(RouteSource routeSource, String message) {
        super(String.format("Error in %s:\n%s", routeSource, message));
        this.routeSource = routeSource;
    }

    public IllegalRouteException(RouteSource routeSource, Throwable throwable) {
        super(String.format("Error in %s:\n%s", routeSource, throwable));
        this.routeSource = routeSource;
    }
}
