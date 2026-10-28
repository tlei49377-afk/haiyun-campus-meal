package com.haiyun.meal.context;

/**
 * 当前登录用户 ID 的线程上下文。
 */
public class BaseContext {

    /**
     * 保存当前请求用户 ID 的线程局部变量。
     */
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 保存当前请求的用户 ID。
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 读取当前请求的用户 ID。
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }

    /**
     * 清理当前请求的用户 ID。
     */
    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
