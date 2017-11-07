package org.hidetake.stubyaml.model;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hidetake.stubyaml.model.execution.CompiledTable;
import org.hidetake.stubyaml.model.execution.CompiledTables;
import org.hidetake.stubyaml.model.yaml.Table;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
@Component
public class TableCompiler {
    private final ExpressionCompiler expressionCompiler;

    public CompiledTables compile(List<Table> tables) {
        return new CompiledTables(tables
            .stream()
            .map(this::compile)
            .filter(Objects::nonNull)
            .collect(toList()));
    }

    public CompiledTable compile(Table table) {
        if (!StringUtils.hasText(table.getName())) {
            log.error("Table name is null, ignored {}", table);
            return null;
        }
        if (!StringUtils.hasText(table.getKey())) {
            log.error("Table key is null, ignored {}", table);
            return null;
        }
        if (ObjectUtils.isEmpty(table.getValues())) {
            log.error("Table values are empty, ignored {}", table);
            return null;
        }
        return CompiledTable.builder()
            .name(table.getName())
            .keyExpression(expressionCompiler.compile(table.getKey()))
            .values(table.getValues())
            .build();
    }
}
