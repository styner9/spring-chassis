package dev.springchassis.core.type.string;

import dev.springchassis.core.exception.UnreachableCodeException;
import dev.springchassis.core.type.string.annotation.CleanText;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

import java.util.Set;

public class CleanTextStringConverter implements ConditionalGenericConverter {

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return targetType.hasAnnotation(CleanText.class);
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Set.of(new ConvertiblePair(String.class, String.class));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        } else if (source instanceof String) {
            var modifiers = CleanTextHelper.toModifiers(targetType.getAnnotation(CleanText.class));
            return StringModifier.applyAll((String) source, modifiers);
        } else {
            throw new UnreachableCodeException();
        }
    }
}
