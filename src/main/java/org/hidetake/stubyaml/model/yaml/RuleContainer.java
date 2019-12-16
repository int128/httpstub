package org.hidetake.stubyaml.model.yaml;

import lombok.*;

import java.util.List;
import java.util.Objects;

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

    public static RuleContainer backport(List<Rule> rules) {
        return RuleContainer.builder()
            .rules(rules)
            .version(Version.V10.name())
            .build();
    }

    public enum Version {

        V10,    //1.0 - rules list
        V11,    //1.1 - rules replaced with container
        LATEST(V11)  //1.1
        ;

        private final Version link;

        Version(Version link) {
            this.link = link;
        }

        Version() {
            this.link = null;
        }

        public static Version of(String text){
            Version version;
            try {
                String localText = text.replaceAll("[^A-z0-9]+", "")
                    .replaceAll("^[^Vv]+", "V")
                    .toUpperCase();
                version = Version.valueOf(localText);
            } catch (IllegalArgumentException e) {
                version = LATEST;
            }

            return Objects.nonNull(version.link) ? version.link : version;
        }

    }

}
