package com.zhanghongshen.common.lock;


import com.zhanghongshen.common.lock.impl.RedisLock;
import com.zhanghongshen.common.lock.impl.ZookeeperLock;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@ConditionalOnBean(RedisLock.class)
@Import({RedisLock.class, ZookeeperLock.class})
public class DistributedLockTemplate {

    private final DistributedLock distributedLock;

    public <T> T execute(String key, long timeout, TimeUnit unit, Supplier<T> action) {
        String value = null;
        try {
            value = distributedLock.tryLock(key, timeout, unit);
            if(value == null) {
                return null;
            }
            return action.get();
        } finally {
            if(value != null) {
                distributedLock.unlock(key, value);
            }
        }
    }

    public void executeWithoutResult(String key, long timeout, TimeUnit unit, Runnable action) {
        this.execute(key, timeout, unit, () -> {
            action.run();
            return null;
        });
    }
}
