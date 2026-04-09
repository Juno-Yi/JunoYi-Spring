package com.junoyi.framework.plugin.domain;

import java.util.Collections;
import java.util.List;

/**
 * 插件描述信息。
 */
public class PluginInfo {

    private final String name;
    private final String version;
    private final List<String> authors;
    private final String website;
    private final String description;
    private final String mainClass;
    private final String basePackage;

    public PluginInfo(String name,
                      String version,
                      List<String> authors,
                      String website,
                      String description,
                      String mainClass,
                      String basePackage) {
        this.name = name;
        this.version = version;
        this.authors = authors == null ? List.of() : List.copyOf(authors);
        this.website = website;
        this.description = description;
        this.mainClass = mainClass;
        this.basePackage = basePackage;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public List<String> getAuthors() {
        return Collections.unmodifiableList(authors);
    }

    public String getWebsite() {
        return website;
    }

    public String getDescription() {
        return description;
    }

    public String getMainClass() {
        return mainClass;
    }

    public String getBasePackage() {
        return basePackage;
    }
}