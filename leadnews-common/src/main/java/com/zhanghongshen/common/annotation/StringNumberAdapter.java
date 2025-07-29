package com.zhanghongshen.common.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import com.zhanghongshen.common.jackson.ConfusionDeserializerModifier;
import com.zhanghongshen.common.jackson.ConfusionSerializerModifier;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Import({ConfusionSerializerModifier.class, ConfusionDeserializerModifier.class})
@Documented
@JacksonAnnotation
public @interface StringNumberAdapter {
}
