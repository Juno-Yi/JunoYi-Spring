package com.junoyi.sdk.context;

import com.junoyi.framework.event.core.EventBus;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.sdk.event.PluginEventBus;
import com.junoyi.sdk.log.PluginLogger;
import com.junoyi.sdk.plugin.PluginInfo;
import org.springframework.context.ApplicationContext;

/**
 * 插件运行上下文。
 */
public interface PluginContext {

    /**
     * 当前插件元信息。
     */
    PluginInfo pluginInfo();

    /**
     * 服务端信息。
     */
    ServerInfo serverInfo();

    /**
     * 当前插件日志门面。
     */
    PluginLogger logger();

    /**
     * 服务端 JunoYiLog。
     */
    JunoYiLog junoYiLog();

    /**
     * SDK 插件事件总线。
     */
    PluginEventBus eventBus();

    /**
     * 服务端事件总线。
     */
    EventBus frameworkEventBus();

    /**
     * Spring 容器。
     */
    ApplicationContext applicationContext();

    /**
     * 获取 Spring Bean。
     */
    default <T> T getBean(Class<T> beanType) {
        return applicationContext().getBean(beanType);
    }
}
