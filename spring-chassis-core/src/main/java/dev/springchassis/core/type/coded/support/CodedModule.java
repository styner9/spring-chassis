package dev.springchassis.core.type.coded.support;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.deser.KeyDeserializers;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import dev.springchassis.core.type.coded.Coded;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * {@link Coded} de/ser 를 위한 jackson module
 *
 * @see Coded
 * @see com.fasterxml.jackson.databind.ser.std.EnumSerializer
 * @see com.fasterxml.jackson.databind.deser.std.EnumDeserializer
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class CodedModule extends SimpleModule {

    public CodedModule() {
        super(CodedModule.class.getSimpleName());
    }

    @Override
    public void setupModule(SetupContext context) {
        context.addSerializers(new CodedSerializers());
        context.addKeySerializers(new CodedKeySerializers());
        context.addDeserializers(new CodedDeserializers());
        context.addKeyDeserializers(new CodedKeyDeserializers());
    }

    private static boolean isTarget(JavaType type) {
        return isTarget(type.getRawClass());
    }

    private static boolean isTarget(Class<?> clazz) {
        return Coded.class.isAssignableFrom(clazz);
    }

    public static class CodedSerializers extends Serializers.Base {
        @Override
        public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
            return isTarget(type) ? new CodedSerializer() : null;
        }

        public static class CodedSerializer extends StdSerializer<Coded> {
            public CodedSerializer() {
                super(Coded.class);
            }

            @Override
            public void serialize(Coded value, JsonGenerator gen, SerializerProvider provider) throws IOException {
                if (value != null) {
                    var code = value.getCode();
                    if (code instanceof Number) {
                        gen.writeNumber(((Number) code).longValue());
                    } else {
                        gen.writeString(code.toString());
                    }
                } else {
                    gen.writeNull();
                }
            }

            @Override
            public void serializeWithType(Coded value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
                WritableTypeId typeId;
                if (value != null) {
                    var shape = value.getCode() instanceof Number ? JsonToken.VALUE_NUMBER_INT : JsonToken.VALUE_STRING;
                    typeId = typeSer.typeId(value, shape);
                } else {
                    typeId = typeSer.typeId(null, JsonToken.VALUE_NULL);
                }

                typeSer.writeTypePrefix(gen, typeId);
                serialize(value, gen, serializers);
                typeSer.writeTypeSuffix(gen, typeId);
            }
        }
    }

    public static class CodedKeySerializers extends Serializers.Base {
        @Override
        public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
            return isTarget(type) ? new CodedKeySerializer() : null;
        }

        static class CodedKeySerializer extends StdSerializer<Coded> {
            public CodedKeySerializer() {
                super(Coded.class);
            }

            @Override
            public void serialize(Coded value, JsonGenerator gen, SerializerProvider provider) throws IOException {
                if (value == null) {
                    throw new IllegalArgumentException();
                }
                gen.writeFieldName(value.getCode().toString());
            }
        }
    }

    public static class CodedDeserializers extends Deserializers.Base {
        @Override
        public JsonDeserializer<?> findEnumDeserializer(Class<?> type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
            return isTarget(type) ? new CodedDeserializer(type) : new PlainEnumDeserializer(type);
        }

        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        static class CodedDeserializer<T extends Enum<T> & Coded<?>> extends JsonDeserializer<T> {
            @NonNull
            private final Class<T> clazz;

            @Override
            public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                return Coded.fromString(clazz, p.getText());
            }
        }

        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        static class PlainEnumDeserializer<T extends Enum<T>> extends JsonDeserializer<T> {
            @NonNull
            private final Class<T> clazz;

            @Override
            public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                return Enum.valueOf(clazz, p.getText());
            }
        }
    }

    public static class CodedKeyDeserializers implements KeyDeserializers {
        @Override
        public KeyDeserializer findKeyDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
            return isTarget(type) ? new CodedKeyDeserializer(type.getRawClass()) : null;
        }

        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        static class CodedKeyDeserializer<T extends Enum<T> & Coded<?>> extends KeyDeserializer {
            @NonNull
            private final Class<T> clazz;

            @Override
            public T deserializeKey(String key, DeserializationContext ctxt) {
                return Coded.fromString(clazz, key);
            }
        }
    }
}
