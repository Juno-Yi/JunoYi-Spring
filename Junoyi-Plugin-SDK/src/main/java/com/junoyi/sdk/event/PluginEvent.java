package com.junoyi.sdk.event;

import java.time.Instant;

/**
 * 插件事件标记接口。
 */
public interface PluginEvent {

    /**
     * 事件发生时间。
     */
    default Instant occurredAt() {
        return Instant.now();
    }
}

