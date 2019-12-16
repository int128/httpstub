package org.hidetake.stubyaml.test.integration

import groovy.transform.Canonical

@Canonical
class Photo {
    String id
    User user
    String filename
    String contentType
}
