package com.junoyi.framework.plugin.spring;

import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.permission.enums.Logical;
import com.junoyi.framework.permission.exception.NoPermissionException;
import com.junoyi.framework.permission.exception.NotLoginException;
import com.junoyi.framework.permission.helper.PermissionHelper;
import com.junoyi.framework.permission.properties.PermissionProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 插件权限拦截器。
 *
 * 仅在插件动态注册的 Controller 场景兜底 Permission 注解校验，
 * 不影响主工程原有 AOP 权限逻辑。
 */
@RequiredArgsConstructor
public class PluginPermissionInterceptor implements HandlerInterceptor {

    private final PermissionProperties properties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        Permission permission = handlerMethod.getMethodAnnotation(Permission.class);
        if (permission == null) {
            permission = handlerMethod.getBeanType().getAnnotation(Permission.class);
        }
        if (permission == null) {
            return true;
        }

        if (!properties.isEnable()) {
            return true;
        }

        checkPermission(permission);
        return true;
    }

    private void checkPermission(Permission permission) {
        if (permission == null) {
            return;
        }

        if (permission.requireLogin()) {
            Long userId = PermissionHelper.getCurrentUserId();
            if (userId == null) {
                throw new NotLoginException();
            }
        }

        if (PermissionHelper.isSuperAdmin()) {
            return;
        }

        String[] requiredPermissions = permission.value();
        if (requiredPermissions.length == 0) {
            return;
        }

        Logical logical = permission.logical();
        boolean hasPermission = PermissionHelper.hasPermissions(requiredPermissions, logical);
        if (!hasPermission) {
            throw new NoPermissionException(requiredPermissions);
        }
    }
}

