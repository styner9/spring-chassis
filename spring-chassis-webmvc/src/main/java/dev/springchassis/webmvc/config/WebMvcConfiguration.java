package dev.springchassis.webmvc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.springchassis.core.type.coded.support.CodedAwareEnumConverterFactory;
import dev.springchassis.core.type.string.CleanTextStringConverter;
import dev.springchassis.web.codec.json.ServerObjectMapperCustomizer;
import dev.springchassis.webmvc.interceptor.ConfigurableHandlerInterceptor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableConfigurationProperties(WebMvcProperties.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired(required = false)
    private List<? extends ServerObjectMapperCustomizer> objectMapperCustomizers;

    @Autowired(required = false)
    private List<? extends ConfigurableHandlerInterceptor> interceptors;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new CodedAwareEnumConverterFactory());
        registry.addConverter(new CleanTextStringConverter());
    }

    @Override
    public void extendMessageConverters(@NonNull List<HttpMessageConverter<?>> converters) {
        if (CollectionUtils.isNotEmpty(objectMapperCustomizers)) {
            var objectMapperToUse = objectMapper.copy();
            objectMapperCustomizers.forEach(customizer -> customizer.customize(objectMapperToUse));

            converters.stream()
                    .filter(obj -> obj instanceof MappingJackson2HttpMessageConverter)
                    .map(obj -> ((MappingJackson2HttpMessageConverter) obj))
                    .forEach(obj -> obj.setObjectMapper(objectMapperToUse));
        }
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        if (CollectionUtils.isNotEmpty(interceptors)) {
            interceptors.forEach(i -> {
                registry.addInterceptor(i)
                        .addPathPatterns(i.getIncludedPathPatterns())
                        .excludePathPatterns(i.getExcludedPathPatterns())
                        .order(i.getOrder());

                if (log.isInfoEnabled()) {
                    log.info("{} configured: include={}, exclude={}",
                            i.getClass().getSimpleName(),
                            i.getIncludedPathPatterns(),
                            i.getExcludedPathPatterns());
                }
            });
        }
    }

}
