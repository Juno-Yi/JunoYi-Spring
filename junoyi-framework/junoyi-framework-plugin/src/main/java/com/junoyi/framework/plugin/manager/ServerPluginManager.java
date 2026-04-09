package com.junoyi.framework.plugin.manager;

import com.junoyi.framework.event.core.EventBus;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.plugin.core.JunoYiJunoYiLoggerAdapter;
import com.junoyi.framework.plugin.core.PluginLoader;
import com.junoyi.framework.plugin.domain.LoadedPlugin;
import com.junoyi.framework.plugin.spring.PluginBeanRegistrar;
import com.junoyi.framework.plugin.spring.PluginServerAdapter;
import com.junoyi.sdk.plugin.JunoYiPlugin;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务器插件管理器。
 */
@Component
public class ServerPluginManager {

    private static final String PLUGIN_DIR = "./plugins";

    private final JunoYiLog log = JunoYiLogFactory.getLogger(ServerPluginManager.class);
    private final ConfigurableApplicationContext applicationContext;
    private final EventBus eventBus;
    private final ObjectProvider<RequestMappingHandlerMapping> handlerMappingProvider;
    private final PluginLoader pluginLoader = new PluginLoader();

    private final Map<String, LoadedPlugin> plugins = new ConcurrentHashMap<>();
    private final Map<String, List<String>> pluginBeans = new ConcurrentHashMap<>();
    private volatile boolean initialized;

    public ServerPluginManager(ConfigurableApplicationContext applicationContext,
                               EventBus eventBus,
                               @Qualifier("requestMappingHandlerMapping")
                               ObjectProvider<RequestMappingHandlerMapping> handlerMappingProvider) {
        this.applicationContext = applicationContext;
        this.eventBus = eventBus;
        this.handlerMappingProvider = handlerMappingProvider;
    }

    public synchronized void init() {
        if (initialized) {
            log.debug("Plugin", "Plugin system already initialized, skip.");
            return;
        }
        initialized = true;

        log.info("Plugin", "Start initializing the plugin system...");
        File pluginDir = initPluginDir();

        File[] jarFiles = pluginDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jarFiles == null || jarFiles.length == 0) {
            log.info("Plugin", "No plugin jars found under {}", pluginDir.getAbsolutePath());
            return;
        }

        List<File> sortedJars = new ArrayList<>(List.of(jarFiles));
        sortedJars.sort(Comparator.comparing(File::getName));

        PluginBeanRegistrar beanRegistrar = new PluginBeanRegistrar(applicationContext, handlerMappingProvider.getIfAvailable());
        DefaultPluginManager defaultPluginManager = new DefaultPluginManager(eventBus, this);

        for (File jarFile : sortedJars) {
            loadSinglePlugin(jarFile, beanRegistrar, defaultPluginManager);
        }

        log.info("Plugin", "All plugins have been initialized. count={}", plugins.size());
    }

    public Collection<String> getLoadedPluginIds() {
        return plugins.keySet();
    }

    public boolean isPluginLoaded(String pluginId) {
        return plugins.containsKey(pluginId);
    }

    private void loadSinglePlugin(File jarFile,
                                  PluginBeanRegistrar beanRegistrar,
                                  DefaultPluginManager defaultPluginManager) {
        try {
            LoadedPlugin loaded = pluginLoader.load(jarFile, applicationContext.getClassLoader());
            String pluginName = loaded.getPluginInfo().getName();
            if (plugins.containsKey(pluginName)) {
                log.warn("Plugin", "Duplicate plugin name {}, skip {}", pluginName, jarFile.getName());
                return;
            }

            JunoYiPlugin plugin = loaded.getPlugin();
            plugin.initialize(new PluginServerAdapter(
                    applicationContext.getId(),
                    new JunoYiJunoYiLoggerAdapter(JunoYiLogFactory.getLogger("Plugin-" + pluginName)),
                    defaultPluginManager,
                    applicationContext
            ), pluginName);

            plugin.onLoad();
            List<String> beanNames = beanRegistrar.register(loaded.getPluginInfo(), loaded.getClassLoader());
            plugin.onEnable();

            plugins.put(pluginName, loaded);
            pluginBeans.put(pluginName, beanNames);
            log.info("Plugin", "Plugin loaded: {} v{} | beans={}",
                    pluginName, loaded.getPluginInfo().getVersion(), beanNames.size());
        } catch (Exception e) {
            log.error("Plugin", "Failed to load plugin jar: " + jarFile.getName(), e);
        }
    }
    @PreDestroy
    public synchronized void destroy() {
        for (LoadedPlugin loadedPlugin : plugins.values()) {
            try {
                loadedPlugin.getPlugin().onDisable();
            } catch (Exception e) {
                log.error("Plugin", "Plugin disable error: " + loadedPlugin.getPluginInfo().getName(), e);
            }

            try {
                loadedPlugin.getClassLoader().close();
            } catch (Exception e) {
                log.error("Plugin", "Plugin classloader close error: " + loadedPlugin.getPluginInfo().getName(), e);
            }
        }
        plugins.clear();
        pluginBeans.clear();
        initialized = false;
    }


    private File initPluginDir() {
        File pluginDir = new File(PLUGIN_DIR);
        if (!pluginDir.exists() && pluginDir.mkdirs()) {
            log.info("Plugin", "Initialize plugin directory: {}", pluginDir.getAbsolutePath());
        }
        return pluginDir;
    }
}
