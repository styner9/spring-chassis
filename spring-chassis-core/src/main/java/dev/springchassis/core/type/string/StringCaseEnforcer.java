package dev.springchassis.core.type.string;

import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
public enum StringCaseEnforcer implements StringModifier {
    NOOP(Function.identity()),
    UPPER(String::toUpperCase),
    LOWER(String::toLowerCase);

    private final Function<String, String> function;

    @Override
    public String apply(String str) {
        return str != null ? function.apply(str) : null;
    }
}
