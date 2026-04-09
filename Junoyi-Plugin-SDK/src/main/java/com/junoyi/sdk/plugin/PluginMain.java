package com.junoyi.sdk.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记插件主类。
 * 框架加载插件时可基于该注解发现入口。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PluginMain {

    /**
     * 插件唯一标识。
     */
    String id();

    /**
     * 插件名称。
     */
    String name() default "";

    /**
     * 插件版本。
     */
    String version() default "";

    /**
     * 插件描述。
     */
    String description() default "";
}

