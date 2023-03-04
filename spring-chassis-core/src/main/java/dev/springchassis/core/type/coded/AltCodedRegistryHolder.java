package dev.springchassis.core.type.coded;

import dev.springchassis.core.context.EagerLoadable;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings({"rawtypes", "unchecked"})
public class AltCodedRegistryHolder implements EagerLoadable {

    private static final CodedRegistry<AltCoded> REGISTRY = CodedRegistry.create(
            AltCoded.class,
            AltCoded::getAltCode
    );

    public <T extends Enum<T> & Coded<?>> T findByOriginalAltCode(@NonNull Class<T> clazz, Object code) {
        return (T) REGISTRY.fromOriginalCode(clazz, code);
    }

    public <T extends Enum<T> & Coded<?>> T findByStringifiedAltCode(@NonNull Class<T> clazz, String code) {
        return (T) REGISTRY.fromStringifiedCode(clazz, code);
    }
}
