package com.junoyi.framework.plugin.manager;

import com.junoyi.framework.event.core.EventBus;
import com.junoyi.framework.plugin.spring.PluginSdkEvent;
import com.junoyi.sdk.event.Event;
import com.junoyi.sdk.plugin.PluginManager;

import java.util.Collection;

/**
 * SDK 插件管理器实现。
 */
public class DefaultPluginManager implements PluginManager {

    private final EventBus eventBus;
    private final ServerPluginManager serverPluginManager;

    public DefaultPluginManager(EventBus eventBus, ServerPluginManager serverPluginManager) {
        this.eventBus = eventBus;
        this.serverPluginManager = serverPluginManager;
    }

    @Override
    public void callEvent(Event event) {
        eventBus.callEvent(new PluginSdkEvent(event));
    }

    @Override
    public Collection<String> getLoadedPluginIds() {
        return serverPluginManager.getLoadedPluginIds();
    }

    @Override
    public boolean isPluginLoaded(String pluginId) {
        return serverPluginManager.isPluginLoaded(pluginId);
    }
}