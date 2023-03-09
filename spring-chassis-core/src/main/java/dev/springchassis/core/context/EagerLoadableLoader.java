package dev.springchassis.core.context;

import dev.springchassis.core.util.ClassUtil;
import io.github.classgraph.ClassInfo;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

import java.util.stream.Collectors;

/**
 * {@link EagerLoadable} 을 스캔하고, spring context 초기화 과정에서 클래스를 적재하는 역할을 수행함
 */
@Slf4j
public class EagerLoadableLoader implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

    public static final int ORDER = SpringChassisContextHolder.ORDER + 1;

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {
        // TODO check class loader
//        if (!SpringChassisContextHolder.isInitialized()) {
//            throw new UnreachableCodeException();
//        }

        var classLoader = EagerLoadableLoader.class.getClassLoader();
        var targetClassNames = ClassUtil.allImplementations(EagerLoadable.class, SpringChassisContextHolder.getBasePackages())
                .stream()
                .map(ClassInfo::getName)
                .collect(Collectors.toList());

        targetClassNames.forEach(name -> {
            try {
                Class.forName(name, true, classLoader);
                if (log.isTraceEnabled()) {
                    log.trace("Loaded eagerly: {}", name);
                }
            } catch (ClassNotFoundException e) {
                log.error("Failed to load class: {}", name, e);
                throw new EagerLoadableLoadingError(e);
            }
        });
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    static final class EagerLoadableLoadingError extends Error {
        EagerLoadableLoadingError(Throwable cause) {
            super(cause);
        }
    }
}
