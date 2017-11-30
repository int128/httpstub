package org.hidetake.stubyaml.model.execution;

import lombok.Data;
import lombok.val;

import java.util.HashMap;
import java.util.List;

@Data
public class CompiledTables {
    private final List<CompiledTable> tables;

    public ResolvedTable resolve(RequestContext requestContext) {
        val map = new HashMap<String, Object>();
        tables.forEach(table -> {
            val tableName = table.getName();
            val tableValue = table.find(requestContext);
            map.put(tableName, tableValue);
        });
        return new ResolvedTable(map);
    }
}
