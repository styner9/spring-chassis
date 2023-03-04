package com.mycompany.domain.greeting;

import dev.springchassis.core.type.coded.Coded;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Greeting implements Coded<String> {
    HI("hi") {
        @Override
        public String buildMessage(String name) {
            return "Hi! " + name;
        }
    },
    HELLO("hello") {
        @Override
        public String buildMessage(String name) {
            return "Hello, " + name + ". Welcome!";
        }
    };

    @Getter
    private final String code;

    public abstract String buildMessage(String name);
}
