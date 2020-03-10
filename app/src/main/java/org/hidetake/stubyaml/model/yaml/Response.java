package org.hidetake.stubyaml.model.yaml;

import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * {@link Response#headers} - Response header.
 * The value must be one of {@link String} or {@link List}.
 *
 * {@link Response#body} - Response body.
 * One of {@code null}, {@link String}, {@link Boolean}, {@link Number}, {@link List} or {@link Map}.
 */
@Data
public class Response {

    private int status = 200;
    private Map<String, Object> headers = Collections.emptyMap();
    private Object body = null;
    private String file = null;
    private List<Table> tables = Collections.emptyList();
    private long delay = 0;

}
