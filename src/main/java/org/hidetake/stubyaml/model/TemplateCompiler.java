package org.hidetake.stubyaml.model;

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
        } catch (CompilationFailedException | ClassNotFoundException | IOException e) {
            log.warn("Invalid expression {}", template, e);
            return null;
        }
    }
}
