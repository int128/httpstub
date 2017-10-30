package org.hidetake.stubyaml.model.execution;

import lombok.Data;
import lombok.val;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ResolvedRequestContext {
    private final Map<String, Object> binding;

    public static ResolvedRequestContext resolve(
        List<CompiledTable> tables,
        RequestContext requestContext
    ) {
        val tableMap = new HashMap<String, Object>(512);
        tables.forEach(table -> {
            val tableName = table.getName();
            val tableValue = table.lookup(requestContext);
            tableMap.put(tableName, tableValue);
        });

        val binding = new HashMap<String, Object>(512);
        binding.putAll(requestContext.getBinding());
        binding.put("table", tableMap);
        return new ResolvedRequestContext(binding);
    }
}
