package com.junoyi.framework.plugin.config;

import com.junoyi.framework.plugin.manager.ServerPluginManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 插件自动配置。
 */
@AutoConfiguration
public class PluginAutoConfiguration {

    @Bean
    public PluginLifecycle pluginLifecycle(ServerPluginManager serverPluginManager) {
        return new PluginLifecycle(serverPluginManager);
    }
}

