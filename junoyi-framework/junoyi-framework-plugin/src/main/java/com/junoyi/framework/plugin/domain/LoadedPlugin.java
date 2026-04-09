package com.junoyi.framework.plugin.domain;

import com.junoyi.sdk.plugin.JunoYiPlugin;

import java.net.URLClassLoader;

/**
 * 已加载插件实例。
 */
public class LoadedPlugin {

    private final PluginInfo pluginInfo;
    private final JunoYiPlugin plugin;
    private final URLClassLoader classLoader;
    private final String jarPath;

    public LoadedPlugin(PluginInfo pluginInfo, JunoYiPlugin plugin, URLClassLoader classLoader, String jarPath) {
        this.pluginInfo = pluginInfo;
        this.plugin = plugin;
        this.classLoader = classLoader;
        this.jarPath = jarPath;
    }

    public PluginInfo getPluginInfo() {
        return pluginInfo;
    }

    public JunoYiPlugin getPlugin() {
        return plugin;
    }

    public URLClassLoader getClassLoader() {
        return classLoader;
    }

    public String getJarPath() {
        return jarPath;
    }
}

