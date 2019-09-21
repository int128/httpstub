package org.hidetake.stubyaml.model.execution;

import groovy.lang.Binding;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class ResponseContext implements Bindable {
    private final RequestContext requestContext;
    private final ResolvedTable resolvedTable;

    @Getter(lazy = true)
    private final Binding binding = createBinding();

    private Binding createBinding() {
        final var binding = requestContext.createBinding();
        binding.setVariable("table", resolvedTable.getMap());
        return binding;
    }
}
