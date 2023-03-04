package dev.springchassis.core.type.coded.support;

import dev.springchassis.core.type.coded.Coded;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

@SuppressWarnings({"unchecked", "rawtypes"})
public class CodedAwareEnumConverterFactory implements ConverterFactory<String, Enum<?>> {

    @Override
    public <T extends Enum<?>> Converter<String, T> getConverter(Class<T> targetType) {
        if (Coded.class.isAssignableFrom(targetType)) {
            return new CodedEnumConverter(targetType);
        } else {
            return new SpringEnumConverter(targetType);
        }
    }

    static class CodedEnumConverter<T extends Enum<T> & Coded<?>> implements Converter<String, T> {

        private final Class<T> clazz;

        public CodedEnumConverter(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        public T convert(String source) {
            return Coded.fromString(clazz, source);
        }
    }

    // copied from StringToEnumConverterFactory
    static class SpringEnumConverter<T extends Enum> implements Converter<String, T> {

        private final Class<T> enumType;

        public SpringEnumConverter(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public T convert(String source) {
            if (source.isEmpty()) {
                // It's an empty enum identifier: reset the enum value to null.
                return null;
            }
            return (T) Enum.valueOf(this.enumType, source.trim());
        }
    }
}
