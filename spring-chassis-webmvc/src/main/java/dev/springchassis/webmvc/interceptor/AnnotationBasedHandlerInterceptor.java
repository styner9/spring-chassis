package dev.springchassis.webmvc.interceptor;

import dev.springchassis.web.method.HandlerMethodCache;
import dev.springchassis.web.method.HandlerMethodScanner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.GenericTypeResolver;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.Set;

public abstract class AnnotationBasedHandlerInterceptor<T extends Annotation>
        extends ConfigurableHandlerInterceptor
        implements ApplicationListener<ContextRefreshedEvent> {

    private final Class<T> annotationType;

    private HandlerMethodCache.AnnotatedHandlerMethodCache<T> cache;

    @SuppressWarnings("unchecked")
    protected AnnotationBasedHandlerInterceptor() {
        annotationType = Optional.ofNullable(GenericTypeResolver.resolveTypeArgument(getClass(), AnnotationBasedHandlerInterceptor.class))
                .map(type -> (Class<T>) type)
                .orElseThrow(IllegalStateException::new);
    }

    @Override
    public final void onApplicationEvent(ContextRefreshedEvent event) {
        var handlerMethods = HandlerMethodScanner.scan(
                event.getApplicationContext(),
                RequestMappingHandlerMapping.class,
                mapping -> mapping.getHandlerMethods().values()
        );
        cache = new HandlerMethodCache.AnnotatedHandlerMethodCache<>(handlerMethods, annotationType);
        doWithHandlerMethods(handlerMethods);
    }

    protected void doWithHandlerMethods(Set<HandlerMethod> handlerMethods) {
        // do nothing
    }

    @Override
    public final boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (isTargetHandler(handler)) {
            T annotation = getAnnotation(handler);
            if (isTargetAnnotation(annotation)) {
                return doPreHandle(request, response, handler, annotation);
            }
        }
        return true;
    }

    protected boolean doPreHandle(HttpServletRequest request, HttpServletResponse response, Object handler, T annotation) throws Exception {
        return true;
    }

    @Override
    public final void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (isTargetHandler(handler)) {
            T annotation = getAnnotation(handler);
            if (isTargetAnnotation(annotation)) {
                doPostHandle(request, response, handler, modelAndView, annotation);
            }
        }
    }

    private boolean isTargetHandler(Object handler) {
        return handler instanceof HandlerMethod;
    }

    protected boolean isTargetAnnotation(T annotation) {
        return annotationType.isInstance(annotation);
    }

    protected final T getAnnotation(Object handler) {
        if (cache == null) {
            throw new IllegalStateException();
        }
        return cache.get((HandlerMethod) handler);
    }

    protected void doPostHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView, T annotation) throws Exception {
        // do nothing
    }

    @Override
    public final void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (isTargetHandler(handler)) {
            T annotation = getAnnotation(handler);
            if (isTargetAnnotation(annotation)) {
                doAfterCompletion(request, response, handler, ex, annotation);
            }
        }
    }

    protected void doAfterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex, T annotation) throws Exception {
        // do nothing
    }

    @Override
    public final void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (isTargetHandler(handler)) {
            T annotation = getAnnotation(handler);
            if (isTargetAnnotation(annotation)) {
                doAfterConcurrentHandlingStarted(request, response, handler, annotation);
            }
        }
    }

    protected void doAfterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler, T annotation) throws Exception {
        // do nothing
    }
}
