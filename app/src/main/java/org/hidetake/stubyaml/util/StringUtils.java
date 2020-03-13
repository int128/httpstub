package org.hidetake.stubyaml.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {

    public static Supplier<String> format(String text, Object... args) {
        return () -> String.format(text, args);
    }

    public static boolean isEmpty(Object text) {
        return text == null || nullToEmpty(text).length() == 0;
    }

    public static String nullToEmpty(Object object) {
        return object == null ? "" : object.toString();
    }

    public static String firstNonEmpty(String... strings) {
        for (String string : strings) {
            if(!isEmpty(string)) {
                return string;
            }
        }

        throw new NullPointerException("All given objects are empty");
    }

}
