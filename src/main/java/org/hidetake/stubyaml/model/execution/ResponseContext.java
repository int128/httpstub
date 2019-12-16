package org.hidetake.stubyaml.model.execution;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.Map;

@Data
@Builder
public class ResponseContext implements Bindable {

    private final RequestContext requestContext;
    private final ResolvedTable resolvedTable;

    @Getter(lazy = true)
    private final Map binding = createBinding();

    @Override
    public Map createBinding() {
        final var binding = requestContext.createBinding();
        binding.put("table", resolvedTable.getMap());
        return binding;
    }

}
