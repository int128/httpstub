package org.hidetake.stubyaml.service.rules;

import org.hidetake.stubyaml.model.yaml.RouteSource;

public interface VersionCompiler {

    Object compile(RouteSource routeSource);

}
