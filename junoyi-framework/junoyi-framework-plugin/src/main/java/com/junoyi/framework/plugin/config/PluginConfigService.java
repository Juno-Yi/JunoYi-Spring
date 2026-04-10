package com.junoyi.framework.plugin.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.junoyi.framework.plugin.domain.LoadedPlugin;
import com.junoyi.sdk.config.PluginConfig;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 插件配置服务（每个插件独立 config.yml）。
 */
@Component
public class PluginConfigService {

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    public PluginConfig create(LoadedPlugin loadedPlugin) {
        String pluginName = loadedPlugin.getPluginInfo().getName();
        Path pluginDataDir = Path.of("./plugins", pluginName);
        Path configFile = pluginDataDir.resolve("config.yml");
        return new PluginConfigImpl(loadedPlugin, pluginDataDir, configFile, yamlMapper);
    }

    private static class PluginConfigImpl implements PluginConfig {

        private final LoadedPlugin loadedPlugin;
        private final Path dataDir;
        private final Path configFile;
        private final ObjectMapper yamlMapper;

        private PluginConfigImpl(LoadedPlugin loadedPlugin, Path dataDir, Path configFile, ObjectMapper yamlMapper) {
            this.loadedPlugin = loadedPlugin;
            this.dataDir = dataDir;
            this.configFile = configFile;
            this.yamlMapper = yamlMapper;
        }

        @Override
        public Path getConfigFile() {
            return configFile;
        }

        @Override
        public boolean exists() {
            return Files.exists(configFile);
        }

        @Override
        public void saveDefaultConfig(String resourcePath) throws IOException {
            if (exists()) {
                return;
            }
            Files.createDirectories(dataDir);
            try (InputStream input = loadedPlugin.getClassLoader().getResourceAsStream(resourcePath)) {
                if (input == null) {
                    throw new IllegalStateException("Plugin default config resource not found: " + resourcePath);
                }
                Files.copy(input, configFile, StandardCopyOption.REPLACE_EXISTING);
            }
        }

        @Override
        public Map<String, Object> load() throws IOException {
            if (!exists()) {
                return new LinkedHashMap<>();
            }
            return yamlMapper.readValue(Files.readString(configFile), MAP_TYPE);
        }

        @Override
        public void save(Map<String, Object> data) throws IOException {
            Files.createDirectories(dataDir);
            yamlMapper.writeValue(configFile.toFile(), data == null ? Map.of() : data);
        }

        @Override
        public Object get(String keyPath, Object defaultValue) throws IOException {
            return ConfigPathUtil.getByPath(load(), keyPath).orElse(defaultValue);
        }

        @Override
        public String getString(String keyPath, String defaultValue) throws IOException {
            Object value = get(keyPath, defaultValue);
            return value == null ? null : String.valueOf(value);
        }

        @Override
        public void set(String keyPath, Object value) throws IOException {
            Map<String, Object> data = load();
            ConfigPathUtil.setByPath(data, keyPath, value);
            save(data);
        }
    }
}

