package org.hidetake.stubyaml.model.exception;

public class StubyamlException extends RuntimeException {

    private StubyamlException(String message) {
        super(message);
    }

    public static StubyamlException of(String message, Object... args) {
        return new StubyamlException(String.format(message, args));
    }

}
