package dev.springchassis.core.type.string;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

@RequiredArgsConstructor
public enum StringTrimmer implements StringModifier {
    NOOP(Function.identity()),
    TO_NULL(StringUtils::trimToNull),
    TO_EMPTY(StringUtils::trimToEmpty);

    private final Function<String, String> function;

    @Override
    public String apply(String str) {
        return str != null ? function.apply(str) : null;
    }
}
