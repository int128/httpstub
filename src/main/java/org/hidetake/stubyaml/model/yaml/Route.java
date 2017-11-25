package org.hidetake.stubyaml.model.yaml;

import lombok.Data;
import org.springframework.http.HttpMethod;

import java.util.List;

@Data
public class Route {
    private final HttpMethod httpMethod;
    private final String requestPath;
    private final List<Rule> rules;
}
