package org.hidetake.stubyaml.model.yaml;

import lombok.Data;

@Data
public class Rule {
    private RequestRule request;
    private ResponseRule response;
}
