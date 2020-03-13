package org.hidetake.stubyaml.app;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hidetake.stubyaml.model.RouteCompiler;
import org.hidetake.stubyaml.model.execution.CompiledRoute;
import org.hidetake.stubyaml.model.yaml.RouteSource;
import org.hidetake.stubyaml.service.RouteSourceParser;
import org.hidetake.stubyaml.util.ExceptionUtils;
import org.hidetake.stubyaml.util.MapUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private final RouteSourceParser routeSourceParser;

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
        final var exceptions = new HashMap<RouteSource, Exception>();
        final var functions = Files.walk(baseDirectory.toPath())
            .filter(path -> path.toFile().isFile())
            .map(path -> routeSourceParser.parse(path.toFile()))
            .map(compile(baseDirectory, exceptions))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .peek(logRoutes())
            .map(compiledRoute -> createRoute(compiledRoute))
            .collect(toList());

        logErrors(exceptions);

        return functions.stream()
            .reduce(indexResponse(functions, exceptions), RouterFunction::and);
    }

    private void logErrors(HashMap<RouteSource, Exception> exceptions) {
        if(!MapUtils.isEmpty(exceptions)) {
            log.info("Errors due compilation:\n{}", exceptionsToString(exceptions));
        }
    }

    private String exceptionsToString(HashMap<RouteSource, Exception> exceptions) {
        return exceptions.entrySet().stream()
            .map(entry -> String.format("%s -> %s",
                entry.getKey().getFile().getName(),
                ExceptionUtils.toChain(entry.getValue())))
            .collect(Collectors.joining("\n"));
    }

    private Consumer<? super CompiledRoute> logRoutes() {
        return compiledRoute ->
            log.info("Registered: {}", compiledRoute.getRequestPredicate());
    }

    private Function<RouteSource, ? extends Optional<CompiledRoute>> compile(File baseDirectory, HashMap<RouteSource, Exception> exceptions) {
        return routeSource -> {
            try {
                return routeCompiler.compile(routeSource, baseDirectory);
            } catch (Exception e) {
                exceptions.put(routeSource, e);
                if(log.isDebugEnabled()) {
                    log.error("Compilation error: ", e);
                }
            }

            return Optional.empty();
        };
    }

    private RouterFunction<ServerResponse> createRoute(CompiledRoute compiledRoute) {
        return RouterFunctions.route(
            compiledRoute.getRequestPredicate(),
            routeHandler.proxy(compiledRoute));
    }

    // TODO: Thymeleaf or groovy
    private RouterFunction<ServerResponse> indexResponse(List<RouterFunction<ServerResponse>> functions,
                                                                HashMap<RouteSource, Exception> exceptions) {
        final var status = String.format(
            "## %d ERROR(S)\n\n%s\n\n## %d ROUTE(S)\n\n%s",
            exceptions.size(),
            String.join("\n----\n", exceptionsToString(exceptions)),
            functions.size(),
            String.join("\n", functionsToString(functions)));

        return RouterFunctions
                .route(GET("/"), request -> ok()
                .syncBody(status));
    }

    private List<String> functionsToString(List<RouterFunction<ServerResponse>> functions) {
        return functions.stream()
            .map(RouterFunction::toString)
            .collect(toList());
    }

    private static RouterFunction<ServerResponse> errorResponse(String cause) {
        return RouterFunctions
            .route(all(), request -> status(HttpStatus.INTERNAL_SERVER_ERROR)
            .syncBody(cause));
    }

}
