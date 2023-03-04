package dev.springchassis.web.method;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.springframework.context.ApplicationContext;
import org.springframework.web.method.HandlerMethod;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@UtilityClass
public class HandlerMethodScanner {

    public <T> Set<HandlerMethod> scan(
            @NonNull ApplicationContext applicationContext,
            @NonNull Class<T> handlerMappingClass,
            @NonNull Function<T, Collection<HandlerMethod>> extractor
    ) {
        var handlerMappings = applicationContext
                .getBeansOfType(handlerMappingClass)
                .values()
                .stream()
                .filter(bean -> bean.getClass().equals(handlerMappingClass))
                .collect(Collectors.toList());

        if (handlerMappings.size() != 1) {
            throw new IllegalStateException();
        }

        var handlerMethods = extractor.apply(handlerMappings.get(0));
        return new HashSet<>(handlerMethods);
    }
}
