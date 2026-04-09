package com.junoyi.framework.plugin.spring;

import com.junoyi.framework.event.core.Event;

/**
 * SDK 事件桥接包装。
 */
public class PluginSdkEvent implements Event {

    private final com.junoyi.sdk.event.Event sdkEvent;

    public PluginSdkEvent(com.junoyi.sdk.event.Event sdkEvent) {
        this.sdkEvent = sdkEvent;
    }

    public com.junoyi.sdk.event.Event getSdkEvent() {
        return sdkEvent;
    }
}

