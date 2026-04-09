package com.junoyi.sdk;


import com.junoyi.sdk.log.JunoYiLogger;
import com.junoyi.sdk.plugin.PluginManager;

/**
 * JunoYi 服务器接口
 *
 * 通过该接口给插件提供更多需要使用的对象实例
 *
 * @author Fan
 */
public interface JunoYiServer {

    /**
     * 服务器名称
     */
    String getServerName();

    /**
     * 获取日志工具
     * @return 日志工具
     */
    JunoYiLogger getLogger();

    /**
     * 获取服务器插件管理器
     * @return 插件管理器
     */
    PluginManager getPluginManager();


}