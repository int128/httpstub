package org.hidetake.stubyaml.model.yaml;

import lombok.Data;

import java.util.Collections;
import java.util.Map;

@Data
public class Config {
    private Logging logging = new Logging();
    private Map<String, Object> constants = Collections.emptyMap();

    @Data
    public static class Logging {
        private boolean headers = true;
        private boolean body = true;
    }
}
