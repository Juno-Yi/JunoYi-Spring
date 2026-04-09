package com.junoyi.framework.plugin.manager;

import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.plugin.core.PluginLoader;
import com.junoyi.sdk.plugin.JunoYiPlugin;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务器插件管理器
 *
 * 负责初始化插件机制，存储已经加载插件
 *
 * @author Fan
 */
@Component
public class ServerPluginManager {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(ServerPluginManager.class);

    /**
     * 插件目录
     */
    private static final String PLUGIN_DIR = "./plugins";

    /**
     * 已经加载的插件
     */
    private final Map<String, JunoYiPlugin> plugins = new HashMap<>();

    /**
     * 插件加载器
     */
    private final PluginLoader pluginLoader = new PluginLoader();

    /**
     * 初始化插件系统
     */
    public void init(){
        log.info("Plugin","Start initializing the plugin system...");

        initPluginDir();

        log.info("Plugin", "All plugins have been initialized.");
    }


    /**
     * 初始化插件目录
     */
    private File initPluginDir(){
        File pluginDir = new File(PLUGIN_DIR);


        if (!pluginDir.exists()){
            boolean created = pluginDir.mkdirs();
            log.info("Plugin","Initialize plugin directory.");
        }

        return pluginDir;
    }
}