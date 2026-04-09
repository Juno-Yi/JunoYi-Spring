package com.junoyi.framework.stater.listener;

import com.junoyi.framework.plugin.manager.ServerPluginManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PluginBootstrapListener implements ApplicationListener<ApplicationReadyEvent> {

    private final ServerPluginManager serverPluginManager;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        serverPluginManager.init();
    }
}