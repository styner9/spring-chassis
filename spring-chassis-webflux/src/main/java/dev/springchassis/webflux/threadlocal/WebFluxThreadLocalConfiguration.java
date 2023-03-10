package dev.springchassis.webflux.threadlocal;

import dev.springchassis.core.threadlocal.ThreadLocalAccessor;
import dev.springchassis.core.threadlocal.ThreadLocalContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class WebFluxThreadLocalConfiguration {

    @Bean
    public ThreadLocalContext threadLocalContext(List<ThreadLocalAccessor<?>> accessors) {
        return new ThreadLocalContext(accessors);
    }

    @Bean
    public static ThreadLocalPreservingDecoratorRegistrar threadLocalPreservingDecoratorRegistrar(ThreadLocalContext context) {
        return new ThreadLocalPreservingDecoratorRegistrar(context);
    }
}
