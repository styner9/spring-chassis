package dev.springchassis.core.type.string.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dev.springchassis.core.type.string.CleanTextStringDeserializer;
import dev.springchassis.core.type.string.StringCaseEnforcer;
import dev.springchassis.core.type.string.StringTrimmer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonDeserialize(using = CleanTextStringDeserializer.class)
public @interface CleanText {

    StringTrimmer trimmer() default StringTrimmer.NOOP;

    StringCaseEnforcer caseEnforcer() default StringCaseEnforcer.NOOP;

    String toBeRemovedChars() default "";

}
