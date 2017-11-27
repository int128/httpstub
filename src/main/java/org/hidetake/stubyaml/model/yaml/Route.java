package org.hidetake.stubyaml.model.yaml;

import lombok.Data;

import java.util.List;

@Data
public class Route {
    private final String httpMethod;
    private final String requestPath;
    private final List<Rule> rules;
}
