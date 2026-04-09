package com.junoyi.sdk.log;

/**
 * 插件日志门面。
 */
public interface PluginLogger {

    void debug(String message);

    void info(String message);

    void warn(String message);

    void error(String message);

    void error(String message, Throwable throwable);
}

