package com.junoyi.sdk.plugin;

import com.junoyi.sdk.context.PluginContext;

/**
 * JunoYi 插件生命周期接口。
 * <p>
 * 为兼容老插件，保留无参生命周期方法；
 * 新插件建议优先实现带 {@link PluginContext} 的方法。
 */
public interface JunoYiPlugin {

    /**
     * 插件加载（推荐）。
     */
    default void onLoad(PluginContext context) {
        onLoad();
    }

    /**
     * 插件启动（推荐）。
     */
    default void onEnable(PluginContext context) {
        onEnable();
    }

    /**
     * 插件停用（推荐）。
     */
    default void onDisable(PluginContext context) {
        onDisable();
    }

    /**
     * @deprecated 请改用 {@link #onLoad(PluginContext)}
     */
    @Deprecated
    default void onLoad() {
    }

    /**
     * @deprecated 请改用 {@link #onEnable(PluginContext)}
     */
    @Deprecated
    default void onEnable() {
    }

    /**
     * @deprecated 请改用 {@link #onDisable(PluginContext)}
     */
    @Deprecated
    default void onDisable() {
    }
}
