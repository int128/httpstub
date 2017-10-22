package org.hidetake.stubyaml;

import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

@RequiredArgsConstructor
public class StubRequestHandler extends AbstractController {
    private final File yamlFile;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.getWriter().append(yamlFile.getAbsolutePath()).close();
        return null;
    }
}
