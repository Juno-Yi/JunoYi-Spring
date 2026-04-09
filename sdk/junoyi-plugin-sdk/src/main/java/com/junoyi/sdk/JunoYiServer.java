package com.junoyi.sdk;


import com.junoyi.sdk.log.JunoYiLogger;
import com.junoyi.sdk.plugin.PluginManager;

/**
 * JunoYi 服务端开放能力。
 */
public interface JunoYiServer {

    String getServerName();

    JunoYiLogger getLogger();

    PluginManager getPluginManager();

    <T> T getBean(Class<T> type);

    boolean containsBean(String beanName);
}