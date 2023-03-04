package dev.springchassis.web.codec.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;

public interface ObjectMapperCustomizer {
    void customize(@NonNull ObjectMapper objectMapper);
}
