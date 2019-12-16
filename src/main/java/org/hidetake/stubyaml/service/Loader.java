package org.hidetake.stubyaml.service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Component
public class Loader {

    @SneakyThrows
    public InputStream load(File file) {
        return new FileInputStream(file);
    }

}
