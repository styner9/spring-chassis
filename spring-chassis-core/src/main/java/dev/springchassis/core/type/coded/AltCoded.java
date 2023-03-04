package dev.springchassis.core.type.coded;

import lombok.NonNull;

/**
 * 외부 시스템과 코드 매핑이 필요할 경우 사용
 * - 내부 시스템 : {@link #getCode()}
 * - 외부 시스템 : {@link #getAltCode()}
 */
public interface AltCoded<T> extends Coded<T> {

    T getAltCode();

    static <T extends Enum<T> & AltCoded<U>, U> T fromCode(@NonNull Class<T> clazz, U code) {
        return CodedRegistryHolder.findByOriginalCode(clazz, code);
    }

    static <T extends Enum<T> & AltCoded<?>> T fromString(@NonNull Class<T> clazz, String code) {
        return CodedRegistryHolder.findByStringifiedCode(clazz, code);
    }

    static <T extends Enum<T> & AltCoded<U>, U> T fromAltCode(@NonNull Class<T> clazz, U code) {
        return AltCodedRegistryHolder.findByOriginalAltCode(clazz, code);
    }

    static <T extends Enum<T> & AltCoded<?>> T fromAltCodeString(@NonNull Class<T> clazz, String code) {
        return AltCodedRegistryHolder.findByStringifiedAltCode(clazz, code);
    }
}
