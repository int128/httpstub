package org.hidetake.stubyaml.model;

import lombok.Data;

import java.util.Map;

@Data
public class Where {
    private String from;
    private String to;
    private Map<String, String> map;
}
