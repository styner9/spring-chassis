package dev.springchassis.core.type.misc.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.springchassis.core.type.misc.PrettyNumberSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = PrettyNumberSerializer.class)
public @interface PrettyNumber {

    /**
     * @see BigDecimal#scale()
     */
    int scale();

    /**
     * @see RoundingMode
     */
    RoundingMode roundingMode() default RoundingMode.HALF_UP;

    /**
     * 빈 문자열은 no formatting
     *
     * @see java.text.DecimalFormat
     */
    String format() default "";

}
