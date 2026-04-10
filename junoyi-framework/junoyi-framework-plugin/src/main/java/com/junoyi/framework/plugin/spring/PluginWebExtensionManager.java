package com.junoyi.framework.plugin.spring;

import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 插件 Web 扩展管理器。
 */
@Component
public class PluginWebExtensionManager {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(PluginWebExtensionManager.class);
    private final CopyOnWriteArrayList<HandlerInterceptor> interceptors = new CopyOnWriteArrayList<>();

    public void registerInterceptor(HandlerInterceptor interceptor) {
        if (interceptor == null) {
            return;
        }
        interceptors.add(interceptor);
        log.info("Plugin", "Plugin interceptor registered: {}", interceptor.getClass().getName());
    }

    public List<HandlerInterceptor> getInterceptors() {
        return List.copyOf(interceptors);
    }
}

