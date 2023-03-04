package dev.springchassis.web.method;

import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;

@UtilityClass
public class HandlerMethodAnnotationScanner {

    private static final Scanner<Class<?>> CLASS_SCANNER = new Scanner<>(null) {
        @Override
        protected <T extends Annotation> T doFind(Class<?> obj, Class<T> annotationType) {
            return AnnotatedElementUtils.findMergedAnnotation(obj, annotationType);
        }
    };

    private static final Scanner<Method> METHOD_SCANNER = new Scanner<>(Sets.newHashSet(ElementType.METHOD)) {
        @Override
        protected <T extends Annotation> T doFind(Method obj, Class<T> annotationType) {
            T annotation = AnnotatedElementUtils.findMergedAnnotation(obj, annotationType);
            if (annotation != null) {
                return annotation;
            } else {
                return AnnotatedElementUtils.findMergedAnnotation(obj.getDeclaringClass(), annotationType);
            }
        }
    };

    private static final Scanner<HandlerMethod> HANDLER_METHOD_SCANNER = new ScannerDelegate<>(METHOD_SCANNER, HandlerMethod::getMethod);

    private static final Scanner<Object> OBJECT_SCANNER = new ScannerDelegate<>(CLASS_SCANNER, Object::getClass);

    public <T extends Annotation> T scan(Object object, Class<T> annotationType) {
        if (object == null || annotationType == null) {
            return null;
        } else if (object instanceof Method) {
            return METHOD_SCANNER.find((Method) object, annotationType);
        } else if (object instanceof Class) {
            return CLASS_SCANNER.find((Class<?>) object, annotationType);
        } else if (object instanceof HandlerMethod) {
            return HANDLER_METHOD_SCANNER.find((HandlerMethod) object, annotationType);
        } else {
            return OBJECT_SCANNER.find(object, annotationType);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    abstract static class Scanner<T> {
        private final Set<ElementType> desiredElementTypes;

        protected abstract <A extends Annotation> A doFind(T obj, Class<A> annotationType);

        final <A extends Annotation> A find(T obj, Class<A> annotationType) {
            A annotation = doFind(obj, annotationType);
            if (annotation == null) {
                return null;
            }

            if (desiredElementTypes == null) {
                return annotation;
            }

            Target targetAnnotation = annotation.annotationType().getAnnotation(Target.class);
            if (targetAnnotation == null || Arrays.stream(targetAnnotation.value()).anyMatch(desiredElementTypes::contains)) {
                return annotation;
            } else {
                return null;
            }
        }
    }

    static final class ScannerDelegate<T, U> extends Scanner<T> {
        private final Scanner<U> delegate;
        private final Function<T, U> converter;

        ScannerDelegate(Scanner<U> delegate, Function<T, U> converter) {
            super(delegate.desiredElementTypes);
            this.delegate = delegate;
            this.converter = converter;
        }

        @Override
        protected <A extends Annotation> A doFind(T obj, Class<A> annotationType) {
            return delegate.doFind(converter.apply(obj), annotationType);
        }
    }
}
