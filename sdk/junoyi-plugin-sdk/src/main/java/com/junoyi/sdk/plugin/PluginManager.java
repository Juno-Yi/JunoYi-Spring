package com.junoyi.sdk.plugin;

import com.junoyi.sdk.event.Event;

import java.util.Collection;

/**
 * 插件管理器。
 */
public interface PluginManager {

    void callEvent(Event event);

    Collection<String> getLoadedPluginIds();

    boolean isPluginLoaded(String pluginId);
}
