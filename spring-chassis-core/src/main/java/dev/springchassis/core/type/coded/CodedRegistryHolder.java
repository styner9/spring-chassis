package dev.springchassis.core.type.coded;

import dev.springchassis.core.context.EagerLoadable;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings({"rawtypes", "unchecked"})
public class CodedRegistryHolder implements EagerLoadable {

    private static final CodedRegistry<Coded> REGISTRY = CodedRegistry.create(
            Coded.class,
            Coded::getCode
    );

    public <T extends Enum<T> & Coded<?>> T findByOriginalCode(@NonNull Class<T> clazz, Object code) {
        return (T) REGISTRY.fromOriginalCode(clazz, code);
    }

    public <T extends Enum<T> & Coded<?>> T findByStringifiedCode(@NonNull Class<T> clazz, String code) {
        return (T) REGISTRY.fromStringifiedCode(clazz, code);
    }
}
