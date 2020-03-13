package org.hidetake.stubyaml.model.yaml;

import lombok.*;

import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class RuleContainer {

    private List<Rule> rules;
    private String version;

    public Version version() {
        return Version.of(version);
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    public static class WithOneRule {

        public Rule rules;
        private String version;

        public RuleContainer toContainer() {
            return RuleContainer.builder()
                .rules(newArrayList(rules))
                .version(version)
                .build();
        }

    }

    public static RuleContainer empty(){
        return RuleContainer.builder()
            .rules(Collections.emptyList())
            .version(Version.LATEST.name())
            .build();
    }

}
