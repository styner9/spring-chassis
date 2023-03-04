package dev.springchassis.core.type.string;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import dev.springchassis.core.type.string.annotation.CleanText;

import java.io.IOException;
import java.util.List;

public class CleanTextStringDeserializer extends StdDeserializer<String> implements ContextualDeserializer {

    @SuppressWarnings("FieldMayBeFinal")
    private List<StringModifier> modifiers;

    @SuppressWarnings("unused")
    public CleanTextStringDeserializer() {
        this(null);
    }

    private CleanTextStringDeserializer(List<StringModifier> modifiers) {
        super(String.class);
        this.modifiers = modifiers;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        var ann = property.getAnnotation(CleanText.class);
        if (ann == null) {
            return null;
        }

        var extractedModifiers = CleanTextHelper.toModifiers(ann);
        if (extractedModifiers.isEmpty()) {
            return null;
        } else {
            return new CleanTextStringDeserializer(extractedModifiers);
        }
    }

    @Override
    public final String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (modifiers == null || modifiers.isEmpty()) {
            throw new IllegalStateException();
        }
        return StringModifier.applyAll(p.getText(), modifiers);
    }
}
