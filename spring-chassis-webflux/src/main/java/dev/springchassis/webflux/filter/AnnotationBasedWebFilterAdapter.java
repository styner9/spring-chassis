package dev.springchassis.webflux.filter;

import dev.springchassis.core.util.BeanUtil;
import dev.springchassis.web.method.HandlerMethodCache.AnnotatedHandlerMethodCache;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.GenericTypeResolver;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Optional;

public abstract class AnnotationBasedWebFilterAdapter<T extends Annotation>
        implements WebFilter, ApplicationListener<ContextRefreshedEvent> {

    private final Class<T> annotationType;

    @Qualifier("requestMappingHandlerMapping")
    private RequestMappingHandlerMapping handlerMapping;

    private AnnotatedHandlerMethodCache<T> handlerMethodCache;

    @SuppressWarnings("unchecked")
    protected AnnotationBasedWebFilterAdapter() {
        annotationType = Optional.ofNullable(GenericTypeResolver.resolveTypeArgument(getClass(), AnnotationBasedWebFilterAdapter.class))
                .map(type -> (Class<T>) type)
                .orElseThrow(IllegalStateException::new);
    }

    @Override
    public final void onApplicationEvent(ContextRefreshedEvent event) {
        var handlerMapping = BeanUtil.exactTypeOf(event.getApplicationContext(), RequestMappingHandlerMapping.class);
        var handlerMethods = new HashSet<>(handlerMapping.getHandlerMethods().values());
        this.handlerMapping = handlerMapping;
        this.handlerMethodCache = new AnnotatedHandlerMethodCache<>(handlerMethods, annotationType);
    }

    @Override
    public final Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return handlerMapping.getHandler(exchange)
                .filter(handler -> handler instanceof HandlerMethod)
                .flatMap(handler -> {
                    var ann = handlerMethodCache.get((HandlerMethod) handler);
                    if (ann != null) {
                        return preHandle(exchange, ann)
                                .flatMap(b -> b ? chain.filter(exchange) : Mono.error(RuntimeException::new))
                                .doOnSuccess(v -> postHandle(exchange, ann).and(afterCompletion(exchange, null, ann)))
                                .doOnError(ex -> afterCompletion(exchange, ex, ann));
                    } else {
                        return chain.filter(exchange);
                    }
                });
    }

    protected Mono<Boolean> preHandle(ServerWebExchange exchange, T ann) {
        return Mono.just(true);
    }

    protected Mono<Void> postHandle(ServerWebExchange exchange, T ann) {
        return Mono.empty();
    }

    protected Mono<Void> afterCompletion(ServerWebExchange exchange, Throwable throwable, T ann) {
        return Mono.empty();
    }
}
