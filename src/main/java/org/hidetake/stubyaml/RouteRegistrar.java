package org.hidetake.stubyaml;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.hidetake.stubyaml.model.RouteCompiler;
import org.hidetake.stubyaml.model.RouteScanner;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class RouteRegistrar {
    private final RouteScanner routeScanner;
    private final RouteCompiler routeCompiler;

    private static final Method controllerMethod;
    static {
        try {
            controllerMethod = RouteController.class.getMethod("handle",
                HttpServletRequest.class,
                Map.class,
                Map.class,
                Map.class,
                Object.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    public void register(RequestMappingHandlerMapping mapping, File baseDirectory) {
        if (baseDirectory.isDirectory()) {
            if (!ObjectUtils.isEmpty(baseDirectory.listFiles())) {
                try {
                    routeScanner.scan(baseDirectory)
                        .map(routeCompiler::compile)
                        .forEach(route -> {
                            val requestMappingInfo = route.getRequestMappingInfo();
                            val controller = new RouteController(route);
                            log.info("Mapping route {}", route);
                            mapping.registerMapping(requestMappingInfo, controller, controllerMethod);
                        });
                } catch (IOException e) {
                    log.warn("Could not scan directory {}", baseDirectory.getAbsolutePath(), e);
                }
            } else {
                log.warn("No rule found in {}", baseDirectory.getAbsolutePath());
            }
        } else {
            log.warn("Not found directory {}", baseDirectory.getAbsolutePath());
        }
    }

    public void unregister(RequestMappingHandlerMapping mapping) {
        new ArrayList<>(mapping.getHandlerMethods().keySet())
            .forEach(requestMappingInfo -> {
                log.info("Unregistering {}", requestMappingInfo);
                mapping.unregisterMapping(requestMappingInfo);
            });
    }
}
