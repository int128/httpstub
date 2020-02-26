package org.hidetake.stubyaml.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionUtils {

    /**
     * Return list of exceptions messages joined via '->' symbols.
     *
     * @param throwable
     * @return
     */
    public static String toChain(Throwable throwable) {
        List<String> result = new ArrayList<>();
        while (throwable != null) {
            if (StringUtils.isEmpty(throwable.getMessage())) {
                result.add(throwable.getClass().getCanonicalName());
            } else {
                result.add(throwable.getMessage());
            }

            throwable = throwable.getCause();
        }

        String output = result.stream()
            .collect(Collectors.joining(" -> "));

        return output;
    }

}
