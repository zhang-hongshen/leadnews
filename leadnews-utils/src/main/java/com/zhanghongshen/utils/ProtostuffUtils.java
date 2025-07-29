package com.zhanghongshen.utils;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.lang.reflect.InvocationTargetException;

public class ProtostuffUtils {

    /**
     * 序列化
     * @param t
     * @param <T>
     * @return
     */
    public static <T> byte[] serialize(T t){
        Schema schema = RuntimeSchema.getSchema(t.getClass());
        return ProtostuffIOUtil.toByteArray(t, schema,
                LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));

    }

    /**
     * 反序列化
     * @param bytes
     * @param c
     * @param <T>
     * @return
     */
    public static <T> T deserialize(byte []bytes, Class<T> c) {
        T t = null;
        try {
            t = c.getDeclaredConstructor().newInstance();
            Schema schema = RuntimeSchema.getSchema(t.getClass());
            ProtostuffIOUtil.mergeFrom(bytes, t, schema);
        } catch (IllegalAccessException | InvocationTargetException
                 | NoSuchMethodException | InstantiationException e) {
            throw new RuntimeException(e);
        }
        return t;
    }

}

