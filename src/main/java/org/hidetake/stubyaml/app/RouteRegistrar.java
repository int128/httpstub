package org.hidetake.stubyaml.app;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.hidetake.stubyaml.model.RouteCompiler;
import org.hidetake.stubyaml.model.yaml.RouteSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.all;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

@Slf4j
@RequiredArgsConstructor
@Component
public class RouteRegistrar {
    private final ReloadableRouter reloadableRouter;
    private final RouteCompiler routeCompiler;
    private final RouteHandler routeHandler;

    public void register(File baseDirectory) {
        reloadableRouter.reload(generateRouterFunction(baseDirectory));
    }

    private RouterFunction<ServerResponse> generateRouterFunction(File baseDirectory) {
        if (!baseDirectory.isDirectory()) {
            return errorResponse(String.format("Directory not found: %s",
                baseDirectory.getAbsolutePath()));
        }
        try {
            List<Exception> exceptions = new ArrayList<>();
            List<RouterFunction<ServerResponse>> functions =
                scan(baseDirectory).flatMap(routeSource -> {
                    try {
                        return routeCompiler.compile(routeSource, baseDirectory)
                            .map(compiledRoute -> Stream.of(
                                RouterFunctions.route(
                                    compiledRoute.getRequestPredicate(),
                                    request -> routeHandler.handle(compiledRoute, request))))
                            .orElse(Stream.empty());
                    } catch (Exception e) {
                        exceptions.add(e);
                        return Stream.empty();
                    }
                }).collect(toList());

            RouterFunction<ServerResponse> index = indexResponse(functions, exceptions);
            return functions.stream().reduce(index, RouterFunction::and);
        } catch (IOException e) {
            return errorResponse(String.format("Error while scanning directory: %s\n%s",
                baseDirectory.getAbsoluteFile(),
                e.toString()));
        }
    }

    private Stream<RouteSource> scan(File baseDirectory) throws IOException {
        log.info("Scanning files in {}", baseDirectory.getAbsolutePath());
        val basePath = baseDirectory.toPath();
        return Files.walk(basePath)
            .filter(path -> path.toFile().isFile())
            .map(path -> new RouteSource(path.toFile()));
    }

    private static RouterFunction<ServerResponse> indexResponse(List<RouterFunction<ServerResponse>> functions, List<Exception> exceptions) {
        // TODO: Thymeleaf
        val status = String.format(
            "## ERRORS\n%s\n\n## ROUTES\n%s",
            String.join("\n", exceptions.stream().map(Throwable::toString).collect(toList())),
            String.join("\n", functions.stream().map(RouterFunction::toString).collect(toList())));
        return RouterFunctions.route(GET("/"), request -> ok().syncBody(status));
    }

    private static RouterFunction<ServerResponse> errorResponse(String cause) {
        return RouterFunctions.route(all(), request -> status(HttpStatus.INTERNAL_SERVER_ERROR).syncBody(cause));
    }
}
