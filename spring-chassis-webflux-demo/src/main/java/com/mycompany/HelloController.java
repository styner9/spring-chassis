package com.mycompany;

import com.mycompany.interceptor.InterceptMe;
import dev.springchassis.web.annotation.RestApiController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@RestApiController
@Component
@Slf4j
public class HelloController {

    @GetMapping("hello")
    public Mono<String> hello() {
        log.info("hello");
        return Mono.just("hello");
    }

    @InterceptMe
    @GetMapping("hello2")
    public Mono<String> hello2() {
        log.info("hello2");
        return Mono.just("hello");
    }
}
