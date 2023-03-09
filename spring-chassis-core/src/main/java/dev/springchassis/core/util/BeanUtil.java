package dev.springchassis.core.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.springframework.context.ApplicationContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.Collectors;

@UtilityClass
public class BeanUtil {

    @Nonnull
    public <T> T exactTypeOf(@NonNull ApplicationContext context, @NonNull Class<T> beanType) {
        return exactTypeOf(context, beanType, null);
    }

    @Nonnull
    public <T> T exactTypeOf(
            @NonNull ApplicationContext context,
            @NonNull Class<T> beanType,
            @Nullable String beanName
    ) {
        var beans = context.getBeansOfType(beanType)
                .entrySet().stream()
                .filter(e -> beanType.equals(e.getValue().getClass()))
                .filter(e -> beanName == null || beanName.equals(e.getKey()))
                .collect(Collectors.toList());
        if (beans.size() != 1) {
            throw new RuntimeException("multiple beans found: " + beanType.getName());
        }
        return beans.iterator().next().getValue();
    }
}
