package com.junoyi.sdk.event;

/**
 * 插件事件监听器。
 *
 * @param <E> 事件类型
 */
@FunctionalInterface
public interface PluginEventListener<E extends PluginEvent> {

    void onEvent(E event);
}

