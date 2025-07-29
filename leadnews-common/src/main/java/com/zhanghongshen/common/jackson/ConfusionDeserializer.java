package com.zhanghongshen.common.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.zhanghongshen.utils.IdsUtils;
import lombok.RequiredArgsConstructor;

import java.io.IOException;


@RequiredArgsConstructor
public class ConfusionDeserializer extends JsonDeserializer<Object> {

    private final JsonDeserializer<Object>  deserializer;
    private final JavaType type;

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        try {
            if(type != null){
                Class<?> rawClass = type.getRawClass();
                if(Long.class.equals(rawClass)){
                    return Long.valueOf(p.getValueAsString());
                } else if(Integer.class.equals(rawClass)){
                    return Integer.valueOf(p.getValueAsString());
                }
            }
            return IdsUtils.decryptLong(p.getValueAsString());
        } catch (Exception e){
            if(deserializer != null){
                return deserializer.deserialize(p, ctxt);
            }
            return p.getCurrentValue();
        }
    }
}
