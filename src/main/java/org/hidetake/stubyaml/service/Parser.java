package org.hidetake.stubyaml.service;

import java.io.File;

public interface Parser {

    <T> T parse(File file, Class<T> clazz);

}
