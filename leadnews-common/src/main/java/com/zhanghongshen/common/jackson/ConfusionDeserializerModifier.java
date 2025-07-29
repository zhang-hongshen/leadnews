package com.zhanghongshen.common.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.zhanghongshen.common.annotation.StringNumberAdapter;

import java.util.Iterator;

public class ConfusionDeserializerModifier extends BeanDeserializerModifier {

    @Override
    public BeanDeserializerBuilder updateBuilder(DeserializationConfig config, BeanDescription beanDescription,
                                                 BeanDeserializerBuilder builder) {
        Iterator<SettableBeanProperty> it = builder.getProperties();
        while (it.hasNext()) {
            SettableBeanProperty p = it.next();
            if (p.getAnnotation(StringNumberAdapter.class) != null) {
                builder.addOrReplaceProperty(p.withValueDeserializer(
                        new ConfusionDeserializer(p.getValueDeserializer(), p.getType())), true);
            }
        }
        return builder;
    }
}
