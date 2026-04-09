package com.junoyi.framework.plugin.config;

import com.junoyi.framework.plugin.manager.ServerPluginManager;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

/**
 * 插件生命周期引导。
 */
public class PluginLifecycle implements ApplicationListener<ApplicationReadyEvent> {

    private final ServerPluginManager serverPluginManager;

    public PluginLifecycle(ServerPluginManager serverPluginManager) {
        this.serverPluginManager = serverPluginManager;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        serverPluginManager.init();
    }
}

