package com.junoyi.sdk.plugin;

/**
 * 插件元信息。
 *
 * @param id          插件ID
 * @param name        插件名称
 * @param version     插件版本
 * @param description 插件描述
 */
public record PluginInfo(String id, String name, String version, String description) {

    public static PluginInfo of(String id) {
        return new PluginInfo(id, "", "", "");
    }
}

