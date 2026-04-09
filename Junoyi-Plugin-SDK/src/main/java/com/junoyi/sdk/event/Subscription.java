package com.junoyi.sdk.event;

/**
 * 事件订阅句柄。
 */
@FunctionalInterface
public interface Subscription {

    /**
     * 取消订阅。
     */
    void unsubscribe();
}

