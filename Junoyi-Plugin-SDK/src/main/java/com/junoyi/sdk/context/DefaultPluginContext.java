package com.junoyi.sdk.context;

import com.junoyi.sdk.event.PluginEventBus;
import com.junoyi.sdk.log.PluginLogger;
import com.junoyi.sdk.plugin.PluginInfo;
import org.springframework.context.ApplicationContext;

import java.util.Objects;

/**
 * 默认插件上下文实现。
 */
public record DefaultPluginContext(
        PluginInfo pluginInfo,
        PluginLogger logger,
        PluginEventBus eventBus,
        ApplicationContext applicationContext
) implements PluginContext {

    public DefaultPluginContext {
        Objects.requireNonNull(pluginInfo, "pluginInfo must not be null");
        Objects.requireNonNull(logger, "logger must not be null");
        Objects.requireNonNull(eventBus, "eventBus must not be null");
        Objects.requireNonNull(applicationContext, "applicationContext must not be null");
    }
}

