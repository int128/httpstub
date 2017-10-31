package org.hidetake.stubyaml;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.hidetake.stubyaml.model.execution.CompiledRoute;
import org.hidetake.stubyaml.model.execution.RequestContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RequiredArgsConstructor
public class StubController {
    private final CompiledRoute route;

    @ResponseBody
    public ResponseEntity handle(
        HttpServletRequest request,
        @RequestHeader Map<String, String> requestHeaders,
        @PathVariable Map<String, String> pathVariables,
        @RequestParam Map<String, String> requestParams,
        @RequestBody(required = false) Object requestBody
    ) {
        val requestContext = RequestContext.builder()
            .request(request)
            .requestHeaders(requestHeaders)
            .pathVariables(pathVariables)
            .requestParams(requestParams)
            .requestBody(requestBody)
            .build();
        return route.getRules().stream()
            .filter(rule -> rule.matches(requestContext))
            .findFirst()
            .map(rule -> rule.createResponseEntity(requestContext))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(String.format("No rule matched for this route %s", route.getRequestMappingInfo())));
    }
}
