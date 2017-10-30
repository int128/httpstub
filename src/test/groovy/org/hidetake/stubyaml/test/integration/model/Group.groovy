package org.hidetake.stubyaml.test.integration.model

import groovy.transform.Canonical

@Canonical
class Group {
    int id
    String name
    List<User> users
}
