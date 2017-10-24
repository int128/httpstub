package org.hidetake.stubyaml;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
public class MessageConvertersConfiguration extends WebMvcConfigurerAdapter {
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new AcceptFormWorkaround());
        super.configureMessageConverters(converters);
    }

    /**
     * A workaround for {@link org.springframework.stereotype.Controller}s
     * with a parameter annotated with {@link org.springframework.web.bind.annotation.RequestBody}
     * to accept application/x-www-form-urlencoded.
     */
    public static class AcceptFormWorkaround implements HttpMessageConverter<Map<String, String>> {
        @Override
        public boolean canRead(Class<?> clazz, MediaType mediaType) {
            return MediaType.APPLICATION_FORM_URLENCODED.includes(mediaType);
        }

        @Override
        public boolean canWrite(Class<?> clazz, MediaType mediaType) {
            return false;
        }

        @Override
        public List<MediaType> getSupportedMediaTypes() {
            return Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED);
        }

        @Override
        public Map<String, String> read(Class<? extends Map<String, String>> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
            return Collections.emptyMap();
        }

        @Override
        public void write(Map<String, String> o, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        }
    }
}
