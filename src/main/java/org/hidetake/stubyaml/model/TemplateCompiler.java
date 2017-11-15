package org.hidetake.stubyaml.model;

import groovy.lang.GroovyRuntimeException;
import groovy.text.SimpleTemplateEngine;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.control.CompilationFailedException;
import org.hidetake.stubyaml.model.execution.CompiledTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class TemplateCompiler {
    public CompiledTemplate compile(String template) {
        if (template == null) {
            return null;
        }
        try {
            return new CompiledTemplate(new SimpleTemplateEngine().createTemplate(template));
        } catch (GroovyRuntimeException | ClassNotFoundException | IOException e) {
            log.warn("Ignored invalid expression {}\n{}", template, e.toString());
            return null;
        }
    }
}
