package org.hidetake.stubyaml.model.yaml;

import lombok.Data;

@Data
public class Rule {
    private String when;
    private Response response;
}
