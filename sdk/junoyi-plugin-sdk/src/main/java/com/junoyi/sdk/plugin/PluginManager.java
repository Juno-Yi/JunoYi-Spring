package com.junoyi.sdk.plugin;

import com.junoyi.sdk.event.Event;

/**
 * 插件管理者
 *
 * @author Fan
 */
public interface PluginManager {

    /**
     * 触发事件
     * @param event 被触发的事件
     */
    void callEvent(Event event);

}
