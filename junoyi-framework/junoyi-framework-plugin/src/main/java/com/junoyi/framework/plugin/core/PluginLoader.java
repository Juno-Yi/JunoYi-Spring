package com.junoyi.framework.plugin.core;

import com.junoyi.framework.plugin.domain.LoadedPlugin;
import com.junoyi.framework.plugin.domain.PluginInfo;
import com.junoyi.sdk.plugin.JunoYiPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 插件加载器。
 */
public class PluginLoader {

    public LoadedPlugin load(File jarFile, ClassLoader parentClassLoader) throws Exception {
        PluginInfo pluginInfo = readPluginInfo(jarFile);
        URLClassLoader pluginClassLoader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()}, parentClassLoader);

        Class<?> mainClass = Class.forName(pluginInfo.getMainClass(), true, pluginClassLoader);
        if (!JunoYiPlugin.class.isAssignableFrom(mainClass)) {
            throw new IllegalStateException("主类未继承 JunoYiPlugin: " + pluginInfo.getMainClass());
        }

        JunoYiPlugin plugin = (JunoYiPlugin) mainClass.getDeclaredConstructor().newInstance();
        return new LoadedPlugin(pluginInfo, plugin, pluginClassLoader, jarFile.getAbsolutePath());
    }

    private PluginInfo readPluginInfo(File jarFile) throws IOException {
        try (JarFile jar = new JarFile(jarFile)) {
            Map<String, String> values = loadFromProperties(jar);
            if (values.isEmpty()) {
                values = loadFromYaml(jar);
            }
            if (values.isEmpty()) {
                throw new IllegalStateException("缺少 plugin.properties 或 plugin.yml: " + jarFile.getName());
            }

            String name = first(values, "name", "plugin-name", "pluginName");
            String version = first(values, "version", "plugin-version", "pluginVersion");
            String mainClass = first(values, "main", "main-class", "mainClass");

            if (name == null || version == null || mainClass == null) {
                throw new IllegalStateException("插件描述缺少必要字段(name/version/main)");
            }

            List<String> authors = parseAuthors(first(values,
                    "authors", "author", "plugin-authors", "plugin-author", "pluginAuthors", "pluginAuthor"));
            String website = first(values, "website", "url", "site", "homepage", "home-page");
            String description = first(values, "description", "desc");

            String basePackage = first(values, "base-package", "basePackage", "scan-package", "scanPackage");
            if (basePackage == null) {
                int idx = mainClass.lastIndexOf('.');
                basePackage = idx > 0 ? mainClass.substring(0, idx) : "";
            }

            return new PluginInfo(name, version, authors, website, description, mainClass, basePackage);
        }
    }

    private Map<String, String> loadFromProperties(JarFile jar) throws IOException {
        JarEntry entry = jar.getJarEntry("plugin.properties");
        if (entry == null) {
            return new HashMap<>();
        }
        Properties properties = new Properties();
        try (InputStream input = jar.getInputStream(entry)) {
            properties.load(input);
        }
        Map<String, String> values = new HashMap<>();
        for (String name : properties.stringPropertyNames()) {
            values.put(name, properties.getProperty(name));
        }
        return values;
    }

    private Map<String, String> loadFromYaml(JarFile jar) throws IOException {
        JarEntry entry = jar.getJarEntry("plugin.yml");
        if (entry == null) {
            entry = jar.getJarEntry("plugin.yaml");
        }
        if (entry == null) {
            return new HashMap<>();
        }

        Map<String, String> values = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(jar.getInputStream(entry), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trim = line.trim();
                if (trim.isEmpty() || trim.startsWith("#") || !trim.contains(":")) {
                    continue;
                }
                String[] parts = trim.split(":", 2);
                values.put(parts[0].trim(), parts[1].trim());
            }
        }
        return values;
    }

    private String first(Map<String, String> values, String... keys) {
        for (String key : keys) {
            String value = values.get(key);
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private List<String> parseAuthors(String authorsRaw) {
        if (authorsRaw == null || authorsRaw.isBlank()) {
            return List.of();
        }
        return Arrays.stream(authorsRaw.split("[,;]"))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toList();
    }
}
