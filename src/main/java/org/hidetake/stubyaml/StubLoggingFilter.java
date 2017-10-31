package org.hidetake.stubyaml;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.stream.Stream;

/**
 * @see org.springframework.web.filter.AbstractRequestLoggingFilter
 * @see ContentCachingRequestWrapper
 * @see ContentCachingResponseWrapper
 */
@Slf4j
@ConditionalOnProperty(value = "no-request-response-log", matchIfMissing = true)
@Component
public class StubLoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isAsyncDispatch(request)) {
            filterChain.doFilter(request, response);
        } else {
            doFilterWrapped(wrapRequest(request), wrapResponse(response), filterChain);
        }
    }

    protected void doFilterWrapped(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, FilterChain filterChain) throws ServletException, IOException {
        beforeRequest(request, response);
        try {
            filterChain.doFilter(request, response);
        }
        finally {
            afterRequest(request, response);
            response.copyBodyToResponse();
        }
    }

    protected void beforeRequest(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
        if (log.isInfoEnabled()) {
            val address = request.getRemoteAddr();
            val queryString = request.getQueryString();
            if (queryString == null) {
                log.info("{}> {} {}", address, request.getMethod(), request.getRequestURI());
            } else {
                log.info("{}> {} {}?{}", address, request.getMethod(), request.getRequestURI(), queryString);
            }
            Collections.list(request.getHeaderNames()).forEach(headerName ->
                Collections.list(request.getHeaders(headerName)).forEach(headerValue ->
                    log.info("{}> {}: {}", address, headerName, headerValue)));
            val content = request.getContentAsByteArray();
            if (content.length > 0) {
                log.info("{}>", address);
                try {
                    val contentString = new String(content, request.getCharacterEncoding());
                    Stream.of(contentString.split("\r\n|\r|\n")).forEach(line -> log.info("{}> {}", address, line));
                } catch (UnsupportedEncodingException e) {
                    log.info("{}> [{} bytes body]", address, content.length);
                }
            }
            log.info("{}>", address);
        }
    }

    protected void afterRequest(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
        if (log.isInfoEnabled()) {
            val address = request.getRemoteAddr();
            val status = response.getStatus();
            log.info("{}< {} {}", address, status, HttpStatus.valueOf(status).getReasonPhrase());
            response.getHeaderNames().forEach(headerName ->
                response.getHeaders(headerName).forEach(headerValue ->
                    log.info("{}< {}: {}", address, headerName, headerValue)));
            val content = response.getContentAsByteArray();
            if (content.length > 0) {
                log.info("{}<", address);
                try {
                    val contentString = new String(content, request.getCharacterEncoding());
                    Stream.of(contentString.split("\r\n|\r|\n")).forEach(line -> log.info("{}< {}", address, line));
                } catch (UnsupportedEncodingException e) {
                    log.info("{}< [{} bytes body]", address, content.length);
                }
            }
        }
    }

    private static ContentCachingRequestWrapper wrapRequest(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            return (ContentCachingRequestWrapper) request;
        } else {
            return new ContentCachingRequestWrapper(request);
        }
    }

    private static ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper) response;
        } else {
            return new ContentCachingResponseWrapper(response);
        }
    }
}
