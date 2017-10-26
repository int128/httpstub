package org.hidetake.stubyaml.model.yaml;

import lombok.Data;

import java.util.Collections;
import java.util.Map;

@Data
public class ResponseRule {
    private int status = 200;
    private Map<String, String> headers = Collections.emptyMap();
    private String body = null;
}
