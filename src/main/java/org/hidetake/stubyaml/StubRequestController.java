package org.hidetake.stubyaml;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.hidetake.stubyaml.model.Rule;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.springframework.util.ObjectUtils.nullSafeToString;

@Slf4j
@RequiredArgsConstructor
public class StubRequestController {
    private static final ResponseEntity<String> NO_RULE_RESPONSE =
        new ResponseEntity<>(HttpStatus.NOT_FOUND);

    private final Rule rule;

    @ResponseBody
    public ResponseEntity handle(
        @PathVariable Map<String, String> pathVariables,
        @RequestParam Map<String, String> requestParams,
        @RequestBody(required = false) Map<String, String> requestBody
    ) throws Exception {
        return Arrays.stream(rule.getRequestAndResponseRules())
            .findAny()
            .map(requestAndResponseRule -> {
                val response = requestAndResponseRule.getResponse();
                val responseBody = replacePlaceholders(response.getBody(),
                    Arrays.asList(pathVariables, requestParams, requestBody));
                return ResponseEntity
                    .status(response.getStatus())
                    .headers(response.getHttpHeaders())
                    .header("x-path-variables", nullSafeToString(pathVariables))
                    .header("x-request-params", nullSafeToString(requestParams))
                    .header("x-request-body", nullSafeToString(requestBody))
                    .body(responseBody);
            })
            .orElse(NO_RULE_RESPONSE);
    }

    private static String replacePlaceholders(String value, List<Map<String, String>> maps) {
        val helper = new PropertyPlaceholderHelper("${", "}");
        return helper.replacePlaceholders(value, key ->
            maps.stream()
                .filter(Objects::nonNull)
                .map(map -> map.get(key))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null)
        );
    }
}
