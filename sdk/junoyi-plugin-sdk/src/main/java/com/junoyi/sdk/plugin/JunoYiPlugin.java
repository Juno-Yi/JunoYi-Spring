package com.junoyi.sdk.plugin;


import com.junoyi.sdk.JunoYiServer;
import com.junoyi.sdk.log.JunoYiLogger;

/**
 * JunoYi 插件抽象基类
 * <p>
 */
public abstract class JunoYiPlugin {

    private JunoYiServer server;
    private JunoYiLogger logger;
    private String pluginId;

    public final void initialize(JunoYiServer server, String pluginId) {
        this.server = server;
        this.logger = server.getLogger();
        this.pluginId = pluginId;
    }

    protected final JunoYiServer getServer() {
        return server;
    }

    protected final JunoYiLogger getLogger() {
        return logger;
    }

    public final String getPluginId() {
        return pluginId;
    }

    public void onLoad() throws Exception {
    }

    public void onEnable() throws Exception {
    }

    public void onDisable() throws Exception {
    }
}
