package com.junoyi.framework.plugin.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 简单配置路径工具（a.b.c）。
 */
public final class ConfigPathUtil {

    private ConfigPathUtil() {
    }

    public static Optional<Object> getByPath(Map<String, Object> data, String path) {
        if (data == null || path == null || path.isBlank()) {
            return Optional.empty();
        }
        String[] parts = path.split("\\.");
        Object current = data;
        for (String part : parts) {
            if (!(current instanceof Map<?, ?> map)) {
                return Optional.empty();
            }
            current = map.get(part);
            if (current == null) {
                return Optional.empty();
            }
        }
        return Optional.of(current);
    }

    @SuppressWarnings("unchecked")
    public static void setByPath(Map<String, Object> data, String path, Object value) {
        if (data == null || path == null || path.isBlank()) {
            return;
        }
        String[] parts = path.split("\\.");
        Map<String, Object> current = data;
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            Object next = current.get(part);
            if (!(next instanceof Map<?, ?>)) {
                Map<String, Object> child = new LinkedHashMap<>();
                current.put(part, child);
                current = child;
            } else {
                current = (Map<String, Object>) next;
            }
        }
        current.put(parts[parts.length - 1], value);
    }
}

