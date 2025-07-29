package com.zhanghongshen.app.gateway.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class MyRateLimiter{

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest().getHeaders().getFirst("userId");
            log.info("[userKeyResolver] userId {}", userId);
            return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("userId"));

        };
    }

}
