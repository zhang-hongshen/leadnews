package com.zhanghongshen.article.annotation;

import com.google.common.util.concurrent.RateLimiter;
import com.zhanghongshen.article.context.UserContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
@Slf4j
public class RateLimitProcessor {

    private final Map<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();

    private final HttpServletRequest request; // Inject HttpServletRequest

    public RateLimitProcessor(HttpServletRequest request) {
        this.request = request;
    }

    @Around("@annotation(rateLimit)")
    public Object limit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        String key = switch (rateLimit.limitType()) {
            case GLOBAL -> "";
            case IP -> getClientIp() + ":";
            case USER_ID -> UserContextHolder.getUserId() + ":";
        } + methodName;
        

        RateLimiter rateLimiter = rateLimiterMap.computeIfAbsent(
                key,
                k -> RateLimiter.create(rateLimit.permitsPerSecond())
        );


        if (!rateLimiter.tryAcquire()) {
            throw new RuntimeException("Too many requests - Rate limit exceeded for method: " + methodName);
        }

        return joinPoint.proceed();
    }

    private String getClientIp() {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }
}
