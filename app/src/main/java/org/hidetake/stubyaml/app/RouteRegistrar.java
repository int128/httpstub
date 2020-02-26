package org.hidetake.stubyaml.app;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hidetake.stubyaml.model.RouteCompiler;
import org.hidetake.stubyaml.model.yaml.FilenameRouteSource;
import org.hidetake.stubyaml.model.yaml.RouteSource;
import org.hidetake.stubyaml.util.ExceptionUtils;
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
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.all;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

@Slf4j
@Component
@RequiredArgsConstructor
public class RouteRegistrar {

    private final ReloadableRouter reloadableRouter;
    private final RouteCompiler routeCompiler;
    private final RouteHandler routeHandler;

    public void register(File baseDirectory) {
        reloadableRouter.reload(generateRouterFunction(baseDirectory));
    }

    //TODO: #RESOURCE_TAG
    private RouterFunction<ServerResponse> generateRouterFunction(File baseDirectory) {
        if (!baseDirectory.isDirectory()) {
            return errorResponse(String.format("Directory not found: %s",
                baseDirectory.getAbsolutePath()));
        }

        try {
            return stubResponse(baseDirectory);
        } catch (IOException e) {
            return errorResponse(String.format("Error while scanning directory: %s\n%s",
                baseDirectory.getAbsoluteFile(),
                e.toString()));
        }
    }

    //TODO: #RESOURCE_TAG
    private RouterFunction<ServerResponse> stubResponse(File baseDirectory) throws IOException {
        log.info("Scanning files in {}", baseDirectory.getAbsolutePath());
        final var exceptions = new ArrayList<Exception>();
        final var functions = Files.walk(baseDirectory.toPath())
            .filter(path -> path.toFile().isFile())
            .map(path -> new FilenameRouteSource(path.toFile()))
            .flatMap(compile(baseDirectory, exceptions))
            .collect(toList());

        return functions.stream()
            .reduce(indexResponse(functions, exceptions), RouterFunction::and);
    }

    //TODO: #RESOURCE_TAG
    private Function<RouteSource, Stream<? extends RouterFunction<ServerResponse>>> compile(
        File baseDirectory,
        List<Exception> exceptions) {
        return routeSource -> {
            try {
                return routeCompiler.compile(routeSource, baseDirectory)
                    .map(compiledRoute -> Stream.of(
                        RouterFunctions.route(
                            compiledRoute.getRequestPredicate(),
                            routeHandler.proxy(compiledRoute))));
            } catch (Exception e) {
                exceptions.add(e);
                return Stream.empty();
            }
        };
    }

    // TODO: Thymeleaf or groovy
    private static RouterFunction<ServerResponse> indexResponse(List<RouterFunction<ServerResponse>> functions,
                                                                List<Exception> exceptions) {
        final var status = String.format(
            "## %d ERROR(S)\n\n%s\n\n## %d ROUTE(S)\n\n%s",
            exceptions.size(),
            String.join("\n----\n", exceptions.stream()
                .map(ExceptionUtils::toChain)
                .collect(toList())),
            functions.size(),
            String.join("\n", functions.stream()
                .map(RouterFunction::toString)
                .collect(toList())));

        return RouterFunctions
                .route(GET("/"), request -> ok()
                .syncBody(status));
    }

    private static RouterFunction<ServerResponse> errorResponse(String cause) {
        return RouterFunctions
            .route(all(), request -> status(HttpStatus.INTERNAL_SERVER_ERROR)
            .syncBody(cause));
    }

}
