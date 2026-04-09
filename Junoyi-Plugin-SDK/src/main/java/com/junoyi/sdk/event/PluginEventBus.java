package com.junoyi.sdk.event;

/**
 * 插件事件总线。
 */
public interface PluginEventBus {

    /**
     * 发布事件。
     */
    void publish(PluginEvent event);

    /**
     * 订阅某类事件。
     */
    <E extends PluginEvent> Subscription subscribe(Class<E> eventType, PluginEventListener<E> listener);
}

