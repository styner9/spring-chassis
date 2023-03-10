package dev.springchassis.webflux;

import dev.springchassis.web.config.WebConfiguration;
import dev.springchassis.webflux.config.WebFluxConfiguration;
import dev.springchassis.webflux.threadlocal.WebFluxThreadLocalConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import({
        WebConfiguration.class,
        WebFluxConfiguration.class,
        WebFluxThreadLocalConfiguration.class
})
public @interface SpringChassisWebFlux {

}
