package org.hidetake.stubyaml.model.exception;

import lombok.Getter;
import org.hidetake.stubyaml.model.yaml.RouteSource;

@Getter
public class IllegalRuleException extends RuntimeException {
    private final RouteSource routeSource;

    public IllegalRuleException(String message, RouteSource routeSource) {
        super(String.format("Error in %s:\n%s", routeSource, message));
        this.routeSource = routeSource;
    }

    public IllegalRuleException(String message, RouteSource routeSource, Throwable cause) {
        super(String.format("Error in %s:\n%s\n%s", routeSource, message, cause), cause);
        this.routeSource = routeSource;
    }
}
