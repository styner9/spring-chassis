package dev.springchassis.core.type.string.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import dev.springchassis.core.type.string.StringTrimmer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@CleanText(trimmer = StringTrimmer.TO_EMPTY)
public @interface TrimToEmpty {

}
