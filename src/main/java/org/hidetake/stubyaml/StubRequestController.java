package org.hidetake.stubyaml;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hidetake.stubyaml.model.Rule;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class StubRequestController {
    private final Rule rule;

    @ResponseBody
    public String handle(
        @RequestParam MultiValueMap<String, String> params,
        @PathVariable Map<String, String> pathVariables
    ) throws Exception {
        return String.format(
            "params=%s\npathVariables=%s\nyaml=%s",
            params,
            pathVariables,
            rule.getYamlFile().getAbsolutePath()
        );
    }
}
