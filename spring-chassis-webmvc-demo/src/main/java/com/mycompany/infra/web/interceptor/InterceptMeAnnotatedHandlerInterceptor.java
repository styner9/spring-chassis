package com.mycompany.infra.web.interceptor;

import dev.springchassis.webmvc.interceptor.AnnotationBasedHandlerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class InterceptMeAnnotatedHandlerInterceptor extends AnnotationBasedHandlerInterceptor<InterceptMe> {
    @Override
    protected boolean doPreHandle(HttpServletRequest request, HttpServletResponse response, Object handler, InterceptMe annotation) throws Exception {
        log.info("PreHandle: {}", handler);
        return true;
    }
}
