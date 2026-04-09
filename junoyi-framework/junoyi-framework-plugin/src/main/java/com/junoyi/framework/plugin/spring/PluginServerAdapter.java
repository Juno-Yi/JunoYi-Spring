package com.junoyi.framework.plugin.spring;

import com.junoyi.sdk.JunoYiServer;
import com.junoyi.sdk.log.JunoYiLogger;
import com.junoyi.sdk.plugin.PluginManager;
import org.springframework.context.ApplicationContext;

/**
 * SDK 服务端适配。
 */
public class PluginServerAdapter implements JunoYiServer {

    private final String serverName;
    private final JunoYiLogger logger;
    private final PluginManager pluginManager;
    private final ApplicationContext applicationContext;

    public PluginServerAdapter(String serverName,
                               JunoYiLogger logger,
                               PluginManager pluginManager,
                               ApplicationContext applicationContext) {
        this.serverName = serverName;
        this.logger = logger;
        this.pluginManager = pluginManager;
        this.applicationContext = applicationContext;
    }

    @Override
    public String getServerName() {
        return serverName;
    }

    @Override
    public JunoYiLogger getLogger() {
        return logger;
    }

    @Override
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    @Override
    public <T> T getBean(Class<T> type) {
        return applicationContext.getBean(type);
    }

    @Override
    public boolean containsBean(String beanName) {
        return applicationContext.containsBean(beanName);
    }
}

