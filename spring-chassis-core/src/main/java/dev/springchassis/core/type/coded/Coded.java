package dev.springchassis.core.type.coded;

import lombok.NonNull;

import javax.annotation.Nonnull;

/**
 * enum 을 조금 더 안전하고 편리하게 사용하기 위해 만든 인터페이스
 * - 자유로운 enum name 리팩토링
 * - case-insensitive request binding (물론 jackson configuration 을 통해 달성 가능하지만)  
 * - request binding 부터 persistence layer 까지 동일한 code 를 사용
 * 
 * @param <T> 코드 타입
 */
public interface Coded<T> {

    @Nonnull
    T getCode();

    static <T extends Enum<T> & Coded<U>, U> T fromCode(@NonNull Class<T> clazz, U code) {
        return CodedRegistryHolder.findByOriginalCode(clazz, code);
    }

    static <T extends Enum<T> & Coded<?>> T fromString(@NonNull Class<T> clazz, String code) {
        return CodedRegistryHolder.findByStringifiedCode(clazz, code);
    }

}
