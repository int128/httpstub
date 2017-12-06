package org.hidetake.stubyaml.model.yaml;

import lombok.Data;

@Data
public class Config {
    private Logging logging = new Logging();

    @Data
    public static class Logging {
        private boolean headers = true;
        private boolean body = true;
    }
}
