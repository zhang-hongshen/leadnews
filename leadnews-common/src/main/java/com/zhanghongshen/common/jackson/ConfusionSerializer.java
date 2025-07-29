package com.zhanghongshen.common.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class ConfusionSerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializers)
            throws IOException {
        if (value != null) {
            jsonGenerator.writeString(value.toString());
            return;
        }
        serializers.defaultSerializeValue(value, jsonGenerator);
    }
}
