package org.hidetake.stubyaml.model.execution;

import lombok.RequiredArgsConstructor;
import lombok.val;

import java.io.File;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.hidetake.stubyaml.util.MapUtils.mapValue;
import static org.springframework.util.ObjectUtils.nullSafeToString;

public interface CompiledResponseBody<T> {
    T evaluate(ResponseContext responseContext);

    class NullBody implements CompiledResponseBody {
        @Override
        public Object evaluate(ResponseContext responseContext) {
            return null;
        }
    }

    @RequiredArgsConstructor
    class PrimitiveBody implements CompiledResponseBody {
        private final Object body;

        @Override
        public Object evaluate(ResponseContext responseContext) {
            return evaluate(body, responseContext);
        }

        private static Object evaluate(Object body, ResponseContext responseContext) {
            if (body == null || body instanceof Number || body instanceof Boolean) {
                return body;
            } else if (body instanceof CompiledExpression) {
                val expression = (CompiledExpression) body;
                val value = expression.evaluate(responseContext);
                return evaluate(value, responseContext);
            } else if (body instanceof List) {
                val list = (List<?>) body;
                return list.stream().map(e -> evaluate(e, responseContext)).collect(toList());
            } else if (body instanceof Map) {
                val map = (Map<?, ?>) body;
                return mapValue(map, v -> evaluate(v, responseContext));
            } else {
                return body.toString();
            }
        }
    }

    @RequiredArgsConstructor
    class FileBody implements CompiledResponseBody<File> {
        private final CompiledExpression filenameExpression;
        private final File baseDirectory;

        @Override
        public File evaluate(ResponseContext responseContext) {
            val filename = nullSafeToString(filenameExpression.evaluate(responseContext));
            val file = new File(baseDirectory, filename);
            if (file.exists()) {
                return file;
            } else {
                throw new IllegalStateException("No such file: " + file.getAbsolutePath());
            }
        }
    }
}
