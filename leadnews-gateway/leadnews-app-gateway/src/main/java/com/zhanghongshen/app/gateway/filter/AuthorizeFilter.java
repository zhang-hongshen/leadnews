package com.zhanghongshen.app.gateway.filter;


import com.zhanghongshen.common.cache.CacheService;
import com.zhanghongshen.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class AuthorizeFilter implements GlobalFilter, Ordered {

    @Autowired
    private CacheService cacheService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // step 1: get request and response object
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // step 2: check whether it is a login request
        if(request.getURI().getPath().contains("/login")){
            return chain.filter(exchange);
        }

        // step 3: extract token from headers
        String token = request.getHeaders().getFirst("token");

        // step 4: check whether token is valid
        JwtUtils tokenStatus = JwtUtils.validateToken(token);
        if(!tokenStatus.isValid()){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        // step 5: add userId extracted from token to request headers
        String userId = String.valueOf(JwtUtils.getPayload(token).get("id"));
        exchange.mutate().request(request.mutate()
                .header("userId", userId)
                .build());

        // step 6: refresh token if needed
        String key = "token:" + userId;
        if (tokenStatus == JwtUtils.REFRESH_NEEDED
                && !token.equals(cacheService.get(key))) {
            // Refresh token
            String refreshedToken = JwtUtils.createToken(userId);
            cacheService.set(key, token, 30, TimeUnit.SECONDS);
            response.getHeaders().add("token", refreshedToken);
            log.info("Token refreshed and new token sent in response headers.");
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
