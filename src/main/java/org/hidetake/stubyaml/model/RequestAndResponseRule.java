package org.hidetake.stubyaml.model;

import lombok.Data;

@Data
public class RequestAndResponseRule {
    private ResponseRule response;
    private Where[] where;
}
