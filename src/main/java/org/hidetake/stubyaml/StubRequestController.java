package org.hidetake.stubyaml;

import lombok.RequiredArgsConstructor;
import org.hidetake.stubyaml.model.Rule;
import org.springframework.web.bind.annotation.ResponseBody;

@RequiredArgsConstructor
public class StubRequestController {
    private final Rule rule;

    @ResponseBody
    public String handle() throws Exception {
        return rule.getYamlFile().getAbsolutePath();
    }
}
