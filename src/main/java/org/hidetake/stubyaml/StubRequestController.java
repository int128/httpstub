package org.hidetake.stubyaml;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.hidetake.stubyaml.model.Rule;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
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
        @RequestParam MultiValueMap<String, String> params,
        @PathVariable Map<String, String> pathVariables
    ) throws Exception {
        return Arrays.stream(rule.getRequestAndResponseRules())
            .map(requestAndResponseRule -> {
                return requestAndResponseRule;
            })
            .findAny()
            .map(requestAndResponseRule -> {
                val response = requestAndResponseRule.getResponse();
                return new ResponseEntity<>(
                    response.getBody(),
                    response.getHttpHeaders(),
                    HttpStatus.valueOf(response.getStatus())
                );
            })
            .orElse(NO_RULE_RESPONSE);
    }
}
