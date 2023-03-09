package dev.springchassis.webmvc.interceptor;

import dev.springchassis.core.util.BeanUtil;
import dev.springchassis.web.method.HandlerMethodCache.AnnotatedHandlerMethodCache;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.GenericTypeResolver;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Optional;

@SuppressWarnings("unchecked")
public abstract class AnnotationBasedHandlerInterceptor<T extends Annotation>
        extends ConfigurableHandlerInterceptor
        implements ApplicationListener<ContextRefreshedEvent> {

    private final Class<T> annotationType;

    private final String requestAttrName;

    private AnnotatedHandlerMethodCache<T> handlerMethodCache;

    protected AnnotationBasedHandlerInterceptor() {
        annotationType = Optional.ofNullable(GenericTypeResolver.resolveTypeArgument(getClass(), AnnotationBasedHandlerInterceptor.class))
                .map(type -> (Class<T>) type)
                .orElseThrow(IllegalStateException::new);
        requestAttrName = annotationType.getName() + ".RESOLVED";
    }

    @Override
    public final void onApplicationEvent(ContextRefreshedEvent event) {
        var handlerMapping = BeanUtil.exactTypeOf(event.getApplicationContext(), RequestMappingHandlerMapping.class);
        var handlerMethods = new HashSet<>(handlerMapping.getHandlerMethods().values());
        this.handlerMethodCache = new AnnotatedHandlerMethodCache<>(handlerMethods, annotationType);
    }

    @Override
    public final boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            T annotation = handlerMethodCache.get((HandlerMethod) handler);
            if (annotation != null) {
                request.setAttribute(requestAttrName, annotation);
                return doPreHandle(request, response, handler, annotation);
            }
        }
        return true;
    }

    @Override
    public final void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        T annotation = (T) request.getAttribute(requestAttrName);
        if (annotation != null) {
            doPostHandle(request, response, handler, modelAndView, annotation);
        }
    }

    @Override
    public final void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        T annotation = (T) request.getAttribute(requestAttrName);
        if (annotation != null) {
            doAfterCompletion(request, response, handler, ex, annotation);
        }
    }

    @Override
    public final void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        T annotation = (T) request.getAttribute(requestAttrName);
        if (annotation != null) {
            doAfterConcurrentHandlingStarted(request, response, handler, annotation);
        }
    }

    protected boolean doPreHandle(HttpServletRequest request, HttpServletResponse response, Object handler, T annotation) throws Exception {
        // override this
        return true;
    }

    protected void doPostHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView, T annotation) throws Exception {
        // override this
    }

    protected void doAfterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex, T annotation) throws Exception {
        // override this
    }

    protected void doAfterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler, T annotation) throws Exception {
        // override this
    }
}
