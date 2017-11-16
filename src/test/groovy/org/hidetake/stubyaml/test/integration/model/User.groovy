package org.hidetake.stubyaml.test.integration.model

import groovy.transform.Canonical

@Canonical
class User {
    int id
    String name
    String description
    int age
    boolean active
}
