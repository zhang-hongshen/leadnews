package com.zhanghongshen.wemedia.context;

final public class UserContextHolder {
    private final static ThreadLocal<Long> WM_USER_THREAD_LOCAL = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        WM_USER_THREAD_LOCAL.set(userId);
    }

    public static Long getUserId() {
        return WM_USER_THREAD_LOCAL.get();
    }

    public static void clear() {
        WM_USER_THREAD_LOCAL.remove();
    }
}
