package org.hidetake.stubyaml.model.execution;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.List;

@Data
@Builder
public class CompiledRoute {
    private final RequestMappingInfo requestMappingInfo;
    private final List<CompiledRule> rules;
}
