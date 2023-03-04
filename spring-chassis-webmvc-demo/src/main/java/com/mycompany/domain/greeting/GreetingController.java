package com.mycompany.domain.greeting;

import com.mycompany.infra.web.interceptor.InterceptMe;
import dev.springchassis.core.type.string.annotation.TrimToEmpty;
import dev.springchassis.web.annotation.RestApiController;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RestApiController
public class GreetingController {

    // intercepted handler example
    @InterceptMe
    @GetMapping("hello")
    public Greeting hello() {
        return Greeting.HELLO;
    }

    // non-intercepted handler example
    @GetMapping("hi")
    public Greeting hi() {
        return Greeting.HI;
    }

    @GetMapping("greeting")
    public String greetingWithRequestParam(
            @RequestParam @TrimToEmpty @NotBlank String name,
            @RequestParam Greeting greeting
    ) {
        return greeting.buildMessage(name);
    }

    @PostMapping("greeting")
    public String greetingWithRequestBody(@RequestBody @Valid Msg msg) {
        return msg.toString();
    }

    @Data
    static class Msg {

        @TrimToEmpty
        @NotBlank
        private String name;

        @NotNull
        private Greeting greeting;

        @Override
        public String toString() {
            return greeting.buildMessage(name);
        }
    }
}
