package dev.springchassis.webmvc.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@ConfigurationProperties("springchassis.webmvc")
@Validated
@Getter
@Setter
public class WebMvcProperties {

    @NotNull
    @Valid
    private Interceptor interceptor = new Interceptor();

    @Getter
    @Setter
    public static class Interceptor {

        @NotNull
        private List<@NotBlank String> inclusion = List.of("/**") ;

        @NotNull
        private List<@NotBlank String> exclusion = List.of("/health", "/actuator/**", "/static/**", "/public/**") ;
    }
}
