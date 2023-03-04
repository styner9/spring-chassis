package dev.springchassis.webmvc;

import dev.springchassis.web.config.WebConfiguration;
import dev.springchassis.webmvc.config.WebMvcConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import({
        WebConfiguration.class,
        WebMvcConfiguration.class
})
public @interface SpringChassisWebMvc {

}
