package org.hidetake.stubyaml;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.hidetake.stubyaml.model.Rule;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class StubRequestController {
    private static final ResponseEntity<String> NO_RULE_RESPONSE =
        new ResponseEntity<>(HttpStatus.NOT_FOUND);

    private final Rule rule;

    @ResponseBody
    public ResponseEntity handle(
        @PathVariable Map<String, String> pathVariables,
        @RequestParam Map<String, String> params
    ) throws Exception {
        return Arrays.stream(rule.getRequestAndResponseRules())
            .map(requestAndResponseRule -> {
                return requestAndResponseRule;
            })
            .findAny()
            .map(requestAndResponseRule -> {
                val response = requestAndResponseRule.getResponse();
                val body = replacePlaceholders(response.getBody(), pathVariables, params);
                return new ResponseEntity<>(
                    body,
                    response.getHttpHeaders(),
                    HttpStatus.valueOf(response.getStatus())
                );
            })
            .orElse(NO_RULE_RESPONSE);
    }

    private static String replacePlaceholders(
        String value,
        Map<String, String> pathVariables,
        Map<String, String> params
    ) {
        val helper = new PropertyPlaceholderHelper("${", "}");
        return helper.replacePlaceholders(
            helper.replacePlaceholders(value, params::get),
            pathVariables::get);
    }
}
