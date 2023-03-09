package com.mycompany.interceptor;

import dev.springchassis.webflux.filter.AnnotationBasedWebFilterAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
@Slf4j
public class InterceptMeAnnotatedHandlerInterceptor extends AnnotationBasedWebFilterAdapter<InterceptMe> {

    @Override
    protected Mono<Boolean> preHandle(ServerWebExchange exchange, InterceptMe ann) {
        log.info("pre");
        return super.preHandle(exchange, ann);
    }

    @Override
    protected Mono<Void> postHandle(ServerWebExchange exchange, InterceptMe ann) {
        log.info("post");
        return super.postHandle(exchange, ann);
    }

    @Override
    protected Mono<Void> afterCompletion(ServerWebExchange exchange, Throwable throwable, InterceptMe ann) {
        log.info("after");
        return super.afterCompletion(exchange, throwable, ann);
    }
}
