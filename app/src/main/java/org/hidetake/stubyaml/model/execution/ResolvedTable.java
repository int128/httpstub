package org.hidetake.stubyaml.model.execution;

import lombok.Data;

import java.util.Map;

@Data
public class ResolvedTable {
    private final Map<String, Object> map;
}
