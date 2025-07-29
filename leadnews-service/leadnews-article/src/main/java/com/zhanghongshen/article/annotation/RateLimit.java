package com.zhanghongshen.article.annotation;


import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Import(RateLimitProcessor.class)
@Documented
public @interface RateLimit {
    int permitsPerSecond();
    LimitType limitType() default LimitType.GLOBAL;
    enum LimitType{
        GLOBAL, IP, USER_ID
    }
}
