package org.hidetake.stubyaml.model.execution;

import lombok.Data;
import lombok.val;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CompiledTables {
    private final List<CompiledTable> tables;

    public Map<String, Object> resolve(RequestContext requestContext) {
        val tableMap = new HashMap<String, Object>(512);
        tables.forEach(table -> {
            val tableName = table.getName();
            val tableValue = table.find(requestContext);
            tableMap.put(tableName, tableValue);
        });

        val binding = new HashMap<String, Object>(512);
        binding.putAll(requestContext.getBinding());
        binding.put("table", tableMap);
        return binding;
    }
}
