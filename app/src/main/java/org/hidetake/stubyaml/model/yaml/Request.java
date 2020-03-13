package org.hidetake.stubyaml.model.yaml;

import lombok.Data;

@Data
public class Request {

    private String path;
    private String method;
    private Boolean relative = false;

}
