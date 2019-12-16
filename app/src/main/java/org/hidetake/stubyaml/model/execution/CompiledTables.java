package org.hidetake.stubyaml.model.execution;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class CompiledTables {
    private final List<CompiledTable> tables;

    public ResolvedTable resolve(RequestContext requestContext) {
        final var map = new HashMap<String, Object>();
        tables.forEach(table -> {
            final var tableName = table.getName();
            final var tableValue = table.find(requestContext);
            map.put(tableName, tableValue);
        });
        return new ResolvedTable(map);
    }
}
