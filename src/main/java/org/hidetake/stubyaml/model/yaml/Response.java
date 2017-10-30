package org.hidetake.stubyaml.model.yaml;

import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
public class Response {
    private int status = 200;
    private Map<String, String> headers = Collections.emptyMap();
    private String body = null;
    private List<Table> tables = Collections.emptyList();
}
