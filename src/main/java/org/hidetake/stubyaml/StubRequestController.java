package org.hidetake.stubyaml;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.hidetake.stubyaml.model.RequestContext;
import org.hidetake.stubyaml.model.execution.CompiledRoute;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class StubRequestController {
    private final CompiledRoute route;

    @ResponseBody
    public ResponseEntity handle(
        @PathVariable Map<String, String> pathVariables,
        @RequestParam Map<String, String> requestParams,
        @RequestBody(required = false) Map<String, Object> requestBody
    ) {
        val requestContext = RequestContext.create(pathVariables, requestParams, requestBody);
        return route.getRules().stream()
            .filter(rule -> rule.matches(requestContext))
            .findFirst()
            .map(rule -> rule.createResponseEntity(requestContext))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
