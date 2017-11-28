package org.hidetake.stubyaml.model.execution;

import lombok.Data;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

@Data
public class ResolvedTable {
    private final Map<String, Object> map;

    public static ResolvedTable of(CompiledTables compiledTables, RequestContextMap requestContextMap) {
        val map = new HashMap<String, Object>();
        compiledTables.getTables().forEach(table -> {
            val tableName = table.getName();
            val tableValue = table.find(requestContextMap);
            map.put(tableName, tableValue);
        });
        return new ResolvedTable(map);
    }
}
