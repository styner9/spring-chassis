package dev.springchassis.web.method;

import lombok.NonNull;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.AbstractMap.SimpleEntry;

public class HandlerMethodCache<K, V> {
    private final Map<K, V> cache;
    private final Function<HandlerMethod, K> keyGenerator;

    public HandlerMethodCache(
            @NonNull Set<HandlerMethod> handlerMethods,
            @NonNull Function<HandlerMethod, K> keyGenerator,
            @NonNull Function<HandlerMethod, V> valueGenerator
    ) {
        this.keyGenerator = keyGenerator;

        this.cache = handlerMethods.stream().map(method -> {
            V value = valueGenerator.apply(method);
            if (value != null) {
                return new AbstractMap.SimpleEntry<>(keyGenerator.apply(method), value);
            } else {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toMap(
                SimpleEntry::getKey,
                SimpleEntry::getValue
        ));
    }

    public final V get(@NonNull HandlerMethod method) {
        return get(keyGenerator.apply(method));
    }

    public final V get(@NonNull K key) {
        return cache.get(key);
    }

    public final boolean contains(@NonNull Object handler) {
        if (handler instanceof HandlerMethod) {
            return get((HandlerMethod) handler) != null;
        }
        return false;
    }

    public final <NK, NV> Map<NK, NV> toMap(@NonNull Function<K, NK> keyMapper, @NonNull Function<V, NV> valueMapper) {
        return cache.entrySet().stream().collect(Collectors.toMap(
                e -> keyMapper.apply(e.getKey()),
                e -> valueMapper.apply(e.getValue())
        ));
    }

    public static class SimpleHandlerMethodCache<T> extends HandlerMethodCache<String, T> {
        public SimpleHandlerMethodCache(Set<HandlerMethod> handlerMethods, Function<HandlerMethod, T> valueGenerator) {
            super(handlerMethods, HandlerMethod::toString, valueGenerator);
        }
    }

    public static class AnnotatedHandlerMethodCache<T extends Annotation> extends HandlerMethodCache<String, T> {

        public AnnotatedHandlerMethodCache(
                @NonNull Set<HandlerMethod> handlerMethods,
                @NonNull Function<HandlerMethod, String> keyGenerator,
                @NonNull Class<T> annotationType,
                @NonNull Predicate<HandlerMethod> handlerMethodFilter
        ) {
            super(handlerMethods, keyGenerator, handlerMethod -> {
                if (handlerMethodFilter.test(handlerMethod)) {
                    return HandlerMethodAnnotationScanner.scan(handlerMethod, annotationType);
                } else {
                    return null;
                }
            });
        }

        public AnnotatedHandlerMethodCache(Set<HandlerMethod> handlerMethods, Function<HandlerMethod, String> keyGenerator, Class<T> annotationType) {
            this(handlerMethods, keyGenerator, annotationType, method -> true);
        }

        public AnnotatedHandlerMethodCache(Set<HandlerMethod> handlerMethods, Class<T> annotationType) {
            this(handlerMethods, HandlerMethod::toString, annotationType, method -> true);
        }
    }
}
