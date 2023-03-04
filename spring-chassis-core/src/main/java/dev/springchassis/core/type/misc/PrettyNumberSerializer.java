package dev.springchassis.core.type.misc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import dev.springchassis.core.type.misc.annotation.PrettyNumber;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings({"unused", "FieldMayBeFinal"})
public class PrettyNumberSerializer extends StdSerializer<Number> implements ContextualSerializer {
    private Integer scale;
    private RoundingMode roundingMode;
    private String format;

    public PrettyNumberSerializer() {
        this(null, null, null);
    }

    public PrettyNumberSerializer(Integer scale, RoundingMode roundingMode, String format) {
        super(Number.class);
        this.scale = scale;
        this.roundingMode = roundingMode;
        this.format = StringUtils.trimToNull(format);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        var ann = property.getAnnotation(PrettyNumber.class);
        if (ann == null) {
            return null;
        }
        return new PrettyNumberSerializer(ann.scale(), ann.roundingMode(), ann.format());
    }

    @Override
    public void serialize(Number value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (scale == null || roundingMode == null) {
            throw new IllegalStateException();
        }
        if (value != null) {
            var dropFractionalParts = false;
            var longCompatible = false;
            BigDecimal original;
            if (value instanceof BigDecimal) {
                original = (BigDecimal) value;
            }
            /*
             *  see NumberFormat#format(Object, StringBuffer, FieldPosition)
             */
            else if (value instanceof Byte
                    || value instanceof Short
                    || value instanceof Integer
                    || value instanceof Long
                    || value instanceof AtomicInteger
                    || value instanceof AtomicLong
                    || (value instanceof BigInteger && ((BigInteger) value).bitLength() < 64)) {
                original = BigDecimal.valueOf(value.longValue());
                dropFractionalParts = scale <= 0;
                longCompatible = true;
            }
            /*
             * see BigDecimal(double)
             *
             * When a double must be used as a source for a BigDecimal,
             * note that this constructor provides an exact conversion;
             * it does not give the same result as converting the double
             * to a String using the Double.toString(double) method and
             * then using the BigDecimal(String) constructor.
             */
            else {
                original = new BigDecimal(value.toString());
                dropFractionalParts = value instanceof BigInteger && scale <= 0;
            }

            var rounded = original.setScale(scale, roundingMode);
            if (format != null) {
                // java.text.DecimalFormat isn't thread-safe
                // use icu4j?
                gen.writeString(new DecimalFormat(format).format(rounded));
            } else if (dropFractionalParts) {
                if (longCompatible) {
                    gen.writeNumber(rounded.longValueExact());
                } else {
                    gen.writeNumber(rounded.toBigIntegerExact());
                }
            } else {
                gen.writeNumber(rounded);
            }
        } else {
            gen.writeNull();
        }
    }
}
