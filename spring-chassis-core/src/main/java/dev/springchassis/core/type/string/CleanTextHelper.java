package dev.springchassis.core.type.string;

import dev.springchassis.core.type.string.annotation.CleanText;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
class CleanTextHelper {

    List<StringModifier> toModifiers(CleanText ann) {
        if (ann == null) {
            return List.of();
        }

        var modifiers = new ArrayList<StringModifier>();
        if (ann.trimmer() != StringTrimmer.NOOP) {
            modifiers.add(ann.trimmer());
        }
        if (ann.caseEnforcer() != StringCaseEnforcer.NOOP) {
            modifiers.add(ann.caseEnforcer());
        }
        if (!ann.toBeRemovedChars().isEmpty()) {
            modifiers.add(str -> StringUtils.replaceChars(str, ann.toBeRemovedChars(), ""));
        }
        return modifiers;
    }
}
