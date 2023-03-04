package dev.springchassis.web.config;

import dev.springchassis.core.jackson.ObjectMapperConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ObjectMapperConfiguration.class)
public class WebConfiguration {

}
