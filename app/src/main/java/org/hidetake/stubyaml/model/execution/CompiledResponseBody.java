package org.hidetake.stubyaml.model.execution;

import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.hidetake.stubyaml.util.MapUtils.mapValue;
import static org.springframework.util.ObjectUtils.nullSafeToString;

public interface CompiledResponseBody<T> {

    T evaluate(ResponseContext responseContext);

    class NullBody implements CompiledResponseBody<Object> {
        @Override
        public Object evaluate(ResponseContext responseContext) {
            return null;
        }

    }

    @RequiredArgsConstructor
    class PrimitiveBody implements CompiledResponseBody<Object> {
        private final Object body;

        private static Object evaluate(Object body, ResponseContext responseContext) {
            if (body == null || body instanceof Number || body instanceof Boolean) {
                return body;
            } else if (body instanceof CompiledExpression) {
                final var expression = (CompiledExpression) body;
                final var value = expression.evaluate(responseContext);
                return evaluate(value, responseContext);
            } else if (body instanceof List) {
                final var list = (List<?>) body;
                return list.stream()
                    .map(e -> evaluate(e, responseContext))
                    .collect(toList());
            } else if (body instanceof Map) {
                final var map = (Map<?, ?>) body;
                return mapValue(map, v -> evaluate(v, responseContext));
            } else {
                return body.toString();
            }
        }

        @Override
        public Object evaluate(ResponseContext responseContext) {
            return evaluate(body, responseContext);
        }

    }

    @RequiredArgsConstructor
    class FileBody implements CompiledResponseBody<File> {

        private final CompiledExpression filenameExpression;
        private final File baseDirectory;

        @Override
        public File evaluate(ResponseContext responseContext) {
            final var filename = nullSafeToString(filenameExpression.evaluate(responseContext));
            final var file = new File(baseDirectory, filename);

            if (file.exists()) {
                return file;
            } else {
                throw new IllegalStateException("No such file: " + file.getAbsolutePath());
            }
        }

    }

}
