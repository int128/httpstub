package org.hidetake.stubyaml.model.yaml;

import lombok.Data;

@Data
public class Rule {

    //TODO: refactor to collection of conditions
    private String when;
    private Response response;

}
