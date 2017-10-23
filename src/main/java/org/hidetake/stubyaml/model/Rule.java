package org.hidetake.stubyaml.model;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

@Data
public class Rule {
    private final String requestPath;
    private final RequestMethod requestMethod;
    private final RequestAndResponseRule[] requestAndResponseRules;

    public RequestMappingInfo toRequestMappingInfo() {
        return new RequestMappingInfo(
            new PatternsRequestCondition(requestPath),
            new RequestMethodsRequestCondition(requestMethod),
            null,
            null,
            null,
            null,
            null
        );
    }
}
