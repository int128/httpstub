package org.hidetake.stubyaml.test.integration.model

import groovy.transform.Canonical

@Canonical
class User {
    int id
    String name
    int age
    boolean active
}
