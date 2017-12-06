package org.hidetake.stubyaml.model.execution;

import groovy.lang.Binding;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.val;

@Data
@Builder
public class ResponseContext implements Bindable {
    private final RequestContext requestContext;
    private final ResolvedTable resolvedTable;

    @Getter(lazy = true)
    private final Binding binding = createBinding();

    private Binding createBinding() {
        val binding = requestContext.createBinding();
        binding.setVariable("table", resolvedTable.getMap());
        return binding;
    }
}
