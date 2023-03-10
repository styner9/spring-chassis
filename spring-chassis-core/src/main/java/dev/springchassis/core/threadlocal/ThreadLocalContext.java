package dev.springchassis.core.threadlocal;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class ThreadLocalContext {

    private static final ThreadLocalAccessor<MdcWrapper> MDC_ACCESSOR = new ThreadLocalAccessor<>(
            MdcWrapper.class,
            MdcWrapper::get,
            MdcWrapper::set,
            MdcWrapper::clear
    );

    private static final List<ThreadLocalAccessor<?>> DEFAULT_ACCESSORS = List.of(MDC_ACCESSOR);

    private final List<ThreadLocalAccessor<?>> accessorList;
    private final Map<Class<?>, ThreadLocalAccessor<?>> accessorMap;

    public ThreadLocalContext() {
        this(null);
    }

    public ThreadLocalContext(List<ThreadLocalAccessor<?>> additionalAccessors) {
        var accessors = Lists.newArrayList(DEFAULT_ACCESSORS);
        if (CollectionUtils.isNotEmpty(additionalAccessors)) {
            accessors.addAll(additionalAccessors);
        }

        this.accessorList = List.copyOf(accessors);
        this.accessorMap = accessors.stream().collect(Collectors.toUnmodifiableMap(
                ThreadLocalAccessor::getObjectType,
                Function.identity())
        );
    }

    public Map<Class<?>, Object> get() {
        // https://bugs.openjdk.java.net/browse/JDK-8148463
        Map<Class<?>, Object> map = Maps.newHashMap();
        accessorMap.forEach((type, accessor) -> {
            var value = Optional.ofNullable(accessor)
                    .map(ThreadLocalAccessor::get)
                    .orElse(null);

            map.put(type, value);
        });
        return map;
    }

    public void set(@NonNull Map<Class<?>, Object> map) {
        log.info("set context");
        accessorMap.forEach((type, accessor) -> accessor.set(map.get(type)));
    }

    public void clear() {
        accessorList.forEach(ThreadLocalAccessor::clear);
    }
}
