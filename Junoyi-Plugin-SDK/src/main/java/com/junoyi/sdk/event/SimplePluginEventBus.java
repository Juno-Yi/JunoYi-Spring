package com.junoyi.sdk.event;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 简单线程安全的事件总线实现。
 */
public class SimplePluginEventBus implements PluginEventBus {

    private final Map<Class<? extends PluginEvent>, CopyOnWriteArrayList<PluginEventListener<? extends PluginEvent>>> listeners = new ConcurrentHashMap<>();

    @Override
    public void publish(PluginEvent event) {
        Objects.requireNonNull(event, "event must not be null");

        listeners.forEach((eventType, eventListeners) -> {
            if (!eventType.isAssignableFrom(event.getClass())) {
                return;
            }
            for (PluginEventListener<? extends PluginEvent> listener : eventListeners) {
                @SuppressWarnings("unchecked")
                PluginEventListener<PluginEvent> typed = (PluginEventListener<PluginEvent>) listener;
                typed.onEvent(event);
            }
        });
    }

    @Override
    public <E extends PluginEvent> Subscription subscribe(Class<E> eventType, PluginEventListener<E> listener) {
        Objects.requireNonNull(eventType, "eventType must not be null");
        Objects.requireNonNull(listener, "listener must not be null");

        listeners.computeIfAbsent(eventType, ignored -> new CopyOnWriteArrayList<>()).add(listener);
        return () -> listeners.computeIfPresent(eventType, (key, value) -> {
            value.remove(listener);
            return value.isEmpty() ? null : value;
        });
    }
}

