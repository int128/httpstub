package org.hidetake.stubyaml.model.yaml;

import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
public class Response {

    private int status = 200;
    /**
     * Response header.
     * The value must be one of {@link String} or {@link List}.
     */
    private Map<String, Object> headers = Collections.emptyMap();
    /**
     * Response body.
     * One of {@code null}, {@link String}, {@link Boolean}, {@link Number}, {@link List} or {@link Map}.
     */
    private Object body = null;
    private String file = null;
    private List<Table> tables = Collections.emptyList();
    private long delay = 0;

}
