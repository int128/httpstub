package org.hidetake.stubyaml.extension

import groovy.json.JsonOutput
import groovy.transform.CompileStatic

@CompileStatic
class JsonExtension {

    static String toJson(final Map self) {
        JsonOutput.toJson(self)
    }

}
