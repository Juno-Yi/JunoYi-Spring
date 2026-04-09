package com.junoyi.framework.plugin.core;

import com.junoyi.sdk.plugin.JunoYiPlugin;

import java.io.File;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 插件加载器
 *
 * @author Fan
 */
public class PluginLoader {


    /**
     * 加载插件
     * @param jarFile jar文件
     * @return 加载好的插件
     * @throws Exception 类型
     */
    public static JunoYiPlugin load(File jarFile) throws Exception {
        JarFile jar = new JarFile(jarFile);
        JarEntry entry = jar.getJarEntry("plugin.yaml");
        if (entry == null) {
            throw new RuntimeException("缺少 plugin.yaml: " + jarFile.getName());
        }
        return null;
    }
}