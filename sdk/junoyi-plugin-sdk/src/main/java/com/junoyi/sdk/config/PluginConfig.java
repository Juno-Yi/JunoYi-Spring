package com.junoyi.sdk.config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * 插件配置操作接口（config.yml）。
 */
public interface PluginConfig {

    Path getConfigFile();

    boolean exists();

    /**
     * 当配置文件不存在时，从插件资源拷贝默认配置。
     *
     * @param resourcePath 资源路径，例如 "config.yml"
     */
    void saveDefaultConfig(String resourcePath) throws IOException;

    /**
     * 读取配置文件内容。
     */
    Map<String, Object> load() throws IOException;

    /**
     * 保存配置内容到文件。
     */
    void save(Map<String, Object> data) throws IOException;

    Object get(String keyPath, Object defaultValue) throws IOException;

    String getString(String keyPath, String defaultValue) throws IOException;

    void set(String keyPath, Object value) throws IOException;
}

