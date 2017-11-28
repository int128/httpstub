package org.hidetake.stubyaml.model.execution;

import lombok.Data;

import java.util.List;

@Data
public class CompiledTables {
    private final List<CompiledTable> tables;

    public ResolvedTable resolve(RequestContextMap requestContextMap) {
        return ResolvedTable.of(this, requestContextMap);
    }
}
