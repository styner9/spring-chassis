package dev.springchassis.core.context;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

import java.util.*;
import java.util.stream.Collectors;

/**
 * spring chassis 컨텍스트 초기화를 위해 필요한 정보를 담는 holder
 * - user base package (spring-chassis 라이브러리를 사용하는 프로젝트의)
 * - app class loader
 */
@Slf4j
public class SpringChassisContextHolder implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

    public static final int ORDER = Ordered.HIGHEST_PRECEDENCE;

    private static final String OWN_BASE_PACKAGE = Optional.of(SpringChassisContextHolder.class.getPackageName())
            .map(str -> str.split("\\."))
            .stream()
            .flatMap(Arrays::stream)
            .limit(2)
            .collect(Collectors.joining("."));

    @Getter
    private static boolean initialized;

    private static Set<String> basePackages;

    private static ClassLoader classLoader;

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (!initialized) {

            basePackages = Sets.newHashSet(OWN_BASE_PACKAGE);

            Optional.ofNullable(applicationContext.getEnvironment().getProperty("springchassis.core.base-packages"))
                    .map(s -> s.split(","))
                    .stream()
                    .flatMap(Arrays::stream)
                    .map(StringUtils::trimToNull)
                    .filter(Objects::nonNull)
                    .forEach(basePackages::add);

            basePackages = Collections.unmodifiableSet(basePackages);

            classLoader = Objects.requireNonNull(applicationContext.getClassLoader());

            if (log.isDebugEnabled()) {
                log.debug("{} initialized: basePackages={}", getClass().getSimpleName(), basePackages);
            }
            initialized = true;
        }
    }

    public static Set<String> getBasePackages() {
        if (!initialized) {
            throw new NotInitializedException();
        }
        return basePackages;
    }

    public static ClassLoader getClassLoader() {
        if (!initialized) {
            throw new NotInitializedException();
        }
        return classLoader;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    public static final class NotInitializedException extends RuntimeException {

    }
}
