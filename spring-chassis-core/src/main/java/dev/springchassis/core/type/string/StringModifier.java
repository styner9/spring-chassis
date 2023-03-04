package dev.springchassis.core.type.string;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.function.Function;

public interface StringModifier extends Function<String, String> {

    static String applyAll(String src, List<StringModifier> modifiers) {
        if (src == null) {
            return null;
        }
        if (CollectionUtils.isEmpty(modifiers)) {
            return src;
        }
        var dst = src;
        for (var modifier : modifiers) {
            dst = modifier.apply(dst);
            if (dst == null) {
                break;
            }
        }
        return dst;
    }
}
