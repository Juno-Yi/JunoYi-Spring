package com.junoyi.framework.plugin.domain;

/**
 * 插件描述信息。
 */
public class PluginInfo {

    private final String id;
    private final String name;
    private final String version;
    private final String mainClass;
    private final String basePackage;

    public PluginInfo(String id, String name, String version, String mainClass, String basePackage) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.mainClass = mainClass;
        this.basePackage = basePackage;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getMainClass() {
        return mainClass;
    }

    public String getBasePackage() {
        return basePackage;
    }
}