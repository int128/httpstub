package org.hidetake.stubyaml.model.yaml;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Data
public class Route {
    private final String requestPath;
    private final RequestMethod requestMethod;
    private final List<Rule> rules;
}
