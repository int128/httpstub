package org.hidetake.stubyaml.model.yaml;

import lombok.Data;

import java.util.Map;

@Data
public class Table {
    private String name;
    private String key;
    private Map<String, Object> values;
}
