package org.hidetake.stubyaml.test.integration

import groovy.transform.Canonical

@Canonical
class Group {
    int id
    String name
    List<User> users
}
