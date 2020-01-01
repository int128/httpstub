package org.hidetake.stubyaml;

import lombok.RequiredArgsConstructor;
import org.gradle.api.logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

@RequiredArgsConstructor
public class Sink implements Runnable {

    private final InputStream stream;
    private final Logger log;
    private final BiConsumer<String, Sink> consumer;
    private final AtomicBoolean logOutput = new AtomicBoolean(Boolean.TRUE);

    @Override
    public void run() {
        Sink instance = Sink.this;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while (logOutput.get() && (line = reader.readLine()) != null) {
                consumer.accept(line, instance);
            }
        } catch (Exception e) {
            log.error("ERROR: {}", e.getMessage());
        } finally {
            try {
                if (null != reader) {
                    reader.close();
                }
            } catch (IOException e) {
                log.error("Unexpected I/O exception closing a stream.", e);
            }
        }
    }

    public void ready() {
        logOutput.set(Boolean.FALSE);
    }

}
