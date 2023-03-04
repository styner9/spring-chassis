package dev.springchassis.core.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import dev.springchassis.core.type.coded.support.CodedModule;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ObjectMapperConfigurer {

    public ObjectMapper configure(@NonNull ObjectMapper mapper) {
        return mapper.copy()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .findAndRegisterModules()
                .registerModules(
                        new CodedModule(),
                        new GuavaModule()
                );
    }

    public ObjectMapper prependAnnotationIntrospector(@NonNull ObjectMapper mapper, @NonNull AnnotationIntrospector introspector) {
        return registerAnnotationIntrospector(mapper, introspector, false);
    }

    public ObjectMapper appendAnnotationIntrospector(@NonNull ObjectMapper mapper, @NonNull AnnotationIntrospector introspector) {
        return registerAnnotationIntrospector(mapper, introspector, true);
    }

    private ObjectMapper registerAnnotationIntrospector(
            @NonNull ObjectMapper mapper,
            @NonNull AnnotationIntrospector introspector,
            boolean append
    ) {
        return mapper.registerModule(new SimpleModule(introspector.getClass().getName() + "Module") {
            @Override
            public void setupModule(SetupContext context) {
                if (append) {
                    context.appendAnnotationIntrospector(introspector);
                } else {
                    context.insertAnnotationIntrospector(introspector);
                }
            }
        });
    }
}
