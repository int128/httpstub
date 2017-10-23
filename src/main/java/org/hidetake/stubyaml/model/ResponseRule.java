package org.hidetake.stubyaml.model;

import lombok.Data;
import org.springframework.http.HttpHeaders;

import java.util.Map;

@Data
public class ResponseRule {
    private int status = 200;
    private HttpHeaders httpHeaders = new HttpHeaders();
    private String body = null;

    public void setHeaders(Map<String, String> map) {
        httpHeaders.clear();
        httpHeaders.setAll(map);
    }

    public Map<String, String> getHeader() {
        return httpHeaders.toSingleValueMap();
    }
}
