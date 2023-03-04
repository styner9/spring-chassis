package dev.springchassis.core.jackson;

import com.fasterxml.jackson.datatype.guava.GuavaModule;
import dev.springchassis.core.type.coded.support.CodedModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * global objectmapper 를 위한 설정
 */
@Configuration
public class ObjectMapperConfiguration {

    @Bean
    public CodedModule codedModule() {
        return new CodedModule();
    }

    @Bean
    public GuavaModule guavaModule() {
        return new GuavaModule();
    }
}
