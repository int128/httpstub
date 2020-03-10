package org.hidetake.stubyaml.model.yaml;

import java.util.Objects;

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
