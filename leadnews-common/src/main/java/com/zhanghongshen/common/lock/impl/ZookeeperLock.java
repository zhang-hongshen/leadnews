package com.zhanghongshen.common.lock.impl;

import com.zhanghongshen.common.lock.DistributedLock;
import lombok.RequiredArgsConstructor;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@ConditionalOnClass(CuratorFramework.class)
@ConditionalOnMissingBean(DistributedLock.class)
public class ZookeeperLock implements DistributedLock {

    private final CuratorFramework client;

    private final Map<String, InterProcessMutex> locks = new ConcurrentHashMap<>();

    @Override
    public String tryLock(String key, long timeout, TimeUnit unit) {
        InterProcessMutex lock = new InterProcessMutex(client, key);
        try {
            String value = null;
            if(lock.acquire(timeout, unit)) {
                value = UUID.randomUUID().toString();
                locks.put(key + value, lock);
            }
            return value;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean unlock(String key, String value) {
        String lockKey = key + value;
        InterProcessMutex lock = locks.get(lockKey);
        if (lock == null) {
            throw new IllegalArgumentException("Lock not found for key: " + key);
        }
        try {
            lock.release();
            locks.remove(lockKey);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to release lock", e);
        }
    }
}
