package dev.springchassis.core.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.springchassis.core.jackson.ObjectMapperConfigurer;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.io.IOException;

@UtilityClass
public class JsonUtil {

    private final ObjectMapper MAPPER = ObjectMapperConfigurer.configure(new ObjectMapper());

    public String toJson(@NonNull Object obj) {
        return toJson(obj, false);
    }

    public String toJson(@NonNull Object obj, boolean pretty) {
        try {
            if (pretty) {
                return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            } else {
                return MAPPER.writeValueAsString(obj);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T fromJson(@NonNull String json, @NonNull Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T fromJson(@NonNull String json, @NonNull TypeReference<T> typeReference) {
        try {
            return MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
