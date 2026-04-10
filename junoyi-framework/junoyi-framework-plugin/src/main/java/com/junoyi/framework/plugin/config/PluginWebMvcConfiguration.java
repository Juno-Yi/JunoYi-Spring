package com.junoyi.framework.plugin.config;

import com.junoyi.framework.permission.properties.PermissionProperties;
import com.junoyi.framework.plugin.spring.PluginPermissionInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 插件 MVC 配置。
 *
 * 为插件动态注册的 Controller 提供 Permission 注解校验能力，
 * 复用主工程 PermissionHelper 逻辑，不改动主工程 AOP。
 */
@AutoConfiguration
@ConditionalOnBean(PermissionProperties.class)
@ConditionalOnProperty(prefix = "junoyi.permission", name = "enable", havingValue = "true", matchIfMissing = true)
public class PluginWebMvcConfiguration implements WebMvcConfigurer {

    private final PermissionProperties permissionProperties;

    public PluginWebMvcConfiguration(PermissionProperties permissionProperties) {
        this.permissionProperties = permissionProperties;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PluginPermissionInterceptor(permissionProperties))
                .addPathPatterns("/**")
                .order(10);
    }
}

