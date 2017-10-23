package org.hidetake.stubyaml;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.hidetake.stubyaml.model.Rule;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.io.File;
import java.util.stream.Stream;

@Slf4j
@Component
public class RuleYamlParser {
    public Stream<Rule> parse(File yamlFile, String requestPath, RequestMethod requestMethod) {
        try {
            val yamlStream = FileUtils.openInputStream(yamlFile);

            return Stream.of(new Rule(
                new RequestMappingInfo(
                    new PatternsRequestCondition(requestPath),
                    new RequestMethodsRequestCondition(requestMethod),
                    null,
                    null,
                    null,
                    null,
                    null
                ),
                yamlFile
            ));
        } finally {
            IOUtils.closeQuietly(yamlStream);
        }
    }
}
