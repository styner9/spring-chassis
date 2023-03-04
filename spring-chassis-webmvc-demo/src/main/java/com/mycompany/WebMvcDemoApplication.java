package com.mycompany;

import dev.springchassis.webmvc.SpringChassisWebMvc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringChassisWebMvc
@SpringBootApplication
public class WebMvcDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebMvcDemoApplication.class, args);
    }
}
