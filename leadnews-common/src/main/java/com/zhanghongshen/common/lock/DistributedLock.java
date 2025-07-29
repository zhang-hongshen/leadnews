package com.zhanghongshen.common.lock;

import java.util.concurrent.TimeUnit;

public interface DistributedLock {

    String tryLock(String key, long timeout, TimeUnit unit);
    boolean unlock(String key, String value);
}
