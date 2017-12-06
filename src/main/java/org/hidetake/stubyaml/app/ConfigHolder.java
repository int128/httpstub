package org.hidetake.stubyaml.app;

import lombok.Data;
import org.hidetake.stubyaml.model.yaml.Config;
import org.springframework.stereotype.Component;

@Data
@Component
public class ConfigHolder {
    private Config config = new Config();

    public void reset() {
        config = new Config();
    }
}
