package org.hidetake.stubyaml.model;

import lombok.RequiredArgsConstructor;
import org.hidetake.stubyaml.model.exception.IllegalRuleException;
import org.hidetake.stubyaml.model.execution.CompiledTable;
import org.hidetake.stubyaml.model.execution.CompiledTables;
import org.hidetake.stubyaml.model.yaml.RouteSource;
import org.hidetake.stubyaml.model.yaml.Table;
import org.hidetake.stubyaml.service.ObjectCompiler;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class TableCompiler implements ObjectCompiler {

    private final ExpressionCompiler expressionCompiler;

    public CompiledTables compile(List<Table> tables, RouteSource source) {
        return new CompiledTables(tables
            .stream()
            .map(table -> compile(table, source))
            .filter(Objects::nonNull)
            .collect(toList()));
    }

    public CompiledTable compile(Table table, RouteSource source) {
        if (!StringUtils.hasText(table.getName())) {
            throw new IllegalRuleException("Table name must not be empty: " + table, source);
        }
        if (!StringUtils.hasText(table.getKey())) {
            throw new IllegalRuleException("Table key must not be empty: " + table, source);
        }
        if (CollectionUtils.isEmpty(table.getValues())) {
            throw new IllegalRuleException("Table values must not be empty: " + table, source);
        }
        return CompiledTable.builder()
            .name(table.getName())
            .keyExpression(expressionCompiler.compileExpression(table.getKey()))
            .values(table.getValues())
            .build();
    }

}
