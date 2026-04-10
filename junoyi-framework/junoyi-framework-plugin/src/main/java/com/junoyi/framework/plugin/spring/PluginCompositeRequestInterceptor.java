package com.junoyi.framework.plugin.spring;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 将插件侧请求拦截器桥接到 Spring MVC。
 */
public class PluginCompositeRequestInterceptor implements HandlerInterceptor {

    private final PluginWebExtensionManager extensionManager;

    public PluginCompositeRequestInterceptor(PluginWebExtensionManager extensionManager) {
        this.extensionManager = extensionManager;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        for (HandlerInterceptor interceptor : extensionManager.getInterceptors()) {
            if (!interceptor.preHandle(request, response, handler)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {
        for (HandlerInterceptor interceptor : extensionManager.getInterceptors()) {
            interceptor.postHandle(request, response, handler, modelAndView);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {
        for (HandlerInterceptor interceptor : extensionManager.getInterceptors()) {
            interceptor.afterCompletion(request, response, handler, ex);
        }
    }
}

