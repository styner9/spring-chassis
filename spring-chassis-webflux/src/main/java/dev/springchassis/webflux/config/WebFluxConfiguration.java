package dev.springchassis.webflux.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.springchassis.core.type.coded.support.CodedAwareEnumConverterFactory;
import dev.springchassis.core.type.string.CleanTextStringConverter;
import dev.springchassis.web.codec.json.ServerObjectMapperCustomizer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.util.List;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class WebFluxConfiguration implements WebFluxConfigurer {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired(required = false)
    private List<? extends ServerObjectMapperCustomizer> objectMapperCustomizers;

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        if (CollectionUtils.isNotEmpty(objectMapperCustomizers)) {
            var objectMapperToUse = objectMapper.copy();
            objectMapperCustomizers.forEach(customizer -> customizer.customize(objectMapperToUse));

            var defaultCodecs = configurer.defaultCodecs();
            defaultCodecs.jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapperToUse));
            defaultCodecs.jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapperToUse));
        }
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new CodedAwareEnumConverterFactory());
        registry.addConverter(new CleanTextStringConverter());
    }
}
