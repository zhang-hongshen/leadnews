package com.zhanghongshen.common.lock.impl;

import com.zhanghongshen.common.cache.CacheService;
import com.zhanghongshen.common.lock.DistributedLock;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
@ConditionalOnClass(StringRedisTemplate.class)
@ConditionalOnMissingBean(DistributedLock.class)
public class RedisLock implements DistributedLock {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public String tryLock(String key, long expire, TimeUnit unit) {
        String token = UUID.randomUUID().toString();
        Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(key, token, expire, unit);
        return result != null && result ? token : null;
    }

    @Override
    public boolean unlock(String key, String value) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then "
                        + "return redis.call('del', KEYS[1]) else return 0 end";
        Object result = stringRedisTemplate.execute((RedisCallback<Object>) (connection) ->
                connection.eval(
                        script.getBytes(),
                        ReturnType.INTEGER,
                        1, // KEYS 的数量
                        key.getBytes(),
                        value.getBytes()
                )
        );
        return "1".equals(result.toString());
    }
}
