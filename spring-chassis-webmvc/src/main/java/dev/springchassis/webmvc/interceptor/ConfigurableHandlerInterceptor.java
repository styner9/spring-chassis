package dev.springchassis.webmvc.interceptor;

import dev.springchassis.webmvc.config.WebMvcProperties;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.annotation.PostConstruct;
import java.util.List;

public abstract class ConfigurableHandlerInterceptor implements AsyncHandlerInterceptor, Ordered {

    @Autowired
    private WebMvcProperties webMvcProperties;

    @Getter
    private List<String> includedPathPatterns;

    @Getter
    private List<String> excludedPathPatterns;

    @PostConstruct
    public void init() {
        var prop = webMvcProperties.getInterceptor();
        this.includedPathPatterns = prop.getInclusion();
        this.excludedPathPatterns = prop.getExclusion();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
