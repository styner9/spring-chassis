package dev.springchassis.core.type.coded;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import dev.springchassis.core.context.SpringChassisContextHolder;
import dev.springchassis.core.util.ClassUtil;
import io.github.classgraph.ClassInfo;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * code -> enum 변환을 위해 미리 구성하는 registry (code-enum mapping)
 */
@Slf4j
@SuppressWarnings({"squid:S1610", "rawtypes"})
abstract class CodedRegistry<T extends Coded> {
    abstract T fromOriginalCode(Class clazz, Object code);

    abstract T fromStringifiedCode(Class clazz, String code);

    static <T extends Coded> CodedRegistry<T> create(
            @NonNull Class<T> interfaceClass,
            @NonNull Function<T, Object> codeGetter
    ) {
        try {
            return CodedRegistryDefaultImpl.create(interfaceClass, codeGetter);
        } catch (SpringChassisContextHolder.NotInitializedException e) {
            log.warn("can't load classes -> use fallback implementation");
            return CodedRegistryFallbackImpl.create(codeGetter);
        } catch (Exception e) {
            throw new Error(e.getMessage(), e);
        }
    }

    /**
     * Enum#values 등을 이용할 경우 array copy 가 발생하므로 성능 저하 우려
     * -> {@link Table} 로 매핑 정보를 구성한 기본 구현체.
     */
    @Value
    @EqualsAndHashCode(callSuper = true)
    @SuppressWarnings({"rawtypes", "unchecked"})
    static class CodedRegistryDefaultImpl<T extends Coded> extends CodedRegistry<T> {
        @NonNull Table<Class<T>, Object, T> originalCodeTable;
        @NonNull Table<Class<T>, String, T> stringifiedCodeTable;

        @Override
        T fromOriginalCode(Class clazz, Object code) {
            return code != null
                    ? originalCodeTable.get(clazz, code)
                    : null;
        }

        @Override
        T fromStringifiedCode(Class clazz, String code) {
            var normalized = normalize(code);
            return normalized != null
                    ? stringifiedCodeTable.get(clazz, normalized)
                    : null;
        }

        static <T extends Coded> CodedRegistry<T> create(
                @NonNull Class<T> interfaceClass,
                @NonNull Function<T, Object> codeGetter
        ) {
            var classLoader = CodedRegistry.class.getClassLoader();

            var classes = ClassUtil.allImplementations(interfaceClass, SpringChassisContextHolder.getBasePackages())
                    .stream()
                    .filter(ClassInfo::isEnum)
                    .map(info -> {
                        try {
                            return (Class<T>) Class.forName(info.getName(), false, classLoader);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toSet());

            Table<Class<T>, Object, T> originalCodeTable = HashBasedTable.create();
            Table<Class<T>, String, T> stringifiedCodeTable = HashBasedTable.create();

            for (var clazz : classes) {
                for (T constant : clazz.getEnumConstants()) {
                    var code = codeGetter.apply(constant);

                    var old = originalCodeTable.put(clazz, code, constant);
                    if (old != null) {
                        throw new RuntimeException(String.format("Duplicated code error [class:%s, code:%s, enum:%s,%s]", clazz.getSimpleName(), code.toString(), old, constant));
                    }

                    old = stringifiedCodeTable.put(clazz, stringify(code), constant);
                    if (old != null) {
                        throw new RuntimeException(String.format("Duplicated code error [class:%s, code:%s, enum:%s,%s]", clazz.getSimpleName(), code.toString(), old, constant));
                    }
                }
            }

            if (originalCodeTable.size() != stringifiedCodeTable.size()) {
                throw new RuntimeException("originalCodeTable#size is different from stringifiedCodeTable#size");
            }

            return new CodedRegistryDefaultImpl<>(
                    ImmutableTable.copyOf(originalCodeTable),
                    ImmutableTable.copyOf(stringifiedCodeTable)
            );
        }

        private static String stringify(Object code) {
            return code != null ? normalize(code.toString()) : null;
        }

        private static String normalize(String code) {
            var trimmed = StringUtils.trimToNull(code);
            return trimmed != null ? trimmed.toLowerCase() : null;
        }
    }

    /**
     * spring context 가 없는 상황 (예: 단위 테스트) 에서 사용하는 구현체
     */
    @Value
    @EqualsAndHashCode(callSuper = true)
    @SuppressWarnings({"unchecked", "rawtypes"})
    static class CodedRegistryFallbackImpl<T extends Coded> extends CodedRegistry<T> {
        Function<T, Object> codeGetter;

        @Override
        T fromOriginalCode(Class clazz, Object code) {
            return code != null
                    ? findFirst(clazz, code::equals)
                    : null;
        }

        @Override
        T fromStringifiedCode(Class clazz, String code) {
            var trimmedCode = StringUtils.trimToNull(code);
            return trimmedCode != null
                    ? findFirst(clazz, e -> trimmedCode.equalsIgnoreCase(e.toString()))
                    : null;
        }

        private T findFirst(Class clazz, Predicate<Object> codePredicate) {
            return (T) Arrays.stream(clazz.getEnumConstants())
                    .filter(e -> {
                        var code = codeGetter.apply((T) e);
                        return codePredicate.test(code);
                    })
                    .findFirst()
                    .orElse(null);
        }

        static <T extends Coded> CodedRegistry<T> create(@NonNull Function<T, Object> codeGetter) {
            return new CodedRegistryFallbackImpl<>(codeGetter);
        }
    }
}
