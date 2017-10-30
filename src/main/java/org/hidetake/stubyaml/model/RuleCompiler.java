package org.hidetake.stubyaml.model;

import groovy.lang.GroovyShell;
import groovy.text.SimpleTemplateEngine;
import groovy.text.TemplateEngine;
import lombok.val;
import org.hidetake.stubyaml.model.execution.CompiledRule;
import org.hidetake.stubyaml.model.execution.Template;
import org.hidetake.stubyaml.model.yaml.Rule;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;

@Component
public class RuleCompiler {
    private final GroovyShell groovyShell = new GroovyShell();
    private final TemplateEngine templateEngine = new SimpleTemplateEngine(groovyShell);

    public CompiledRule compile(Rule rule) {
        val response = rule.getResponse();
        return CompiledRule.builder()
            .when(ofNullable(rule.getWhen()).map(groovyShell::parse).orElse(null))
            .status(response.getStatus())
            .headers(response.getHeaders()
                .entrySet()
                .stream()
                .collect(toMap(Map.Entry::getKey, entry -> toTemplate(entry.getValue()))))
            .body(toTemplate(response.getBody()))
            .build();
    }

    private Template toTemplate(String expression) {
        try {
            return new Template(templateEngine.createTemplate(expression));
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
