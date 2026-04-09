package com.junoyi.framework.plugin.spring;

import com.junoyi.framework.plugin.domain.PluginInfo;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * 将插件中的 Spring 组件注册到主容器。
 */
public class PluginBeanRegistrar {

    private final ConfigurableApplicationContext applicationContext;
    private final RequestMappingHandlerMapping handlerMapping;

    public PluginBeanRegistrar(ConfigurableApplicationContext applicationContext,
                               RequestMappingHandlerMapping handlerMapping) {
        this.applicationContext = applicationContext;
        this.handlerMapping = handlerMapping;
    }

    public List<String> register(PluginInfo pluginInfo, ClassLoader classLoader) throws Exception {
        List<String> names = new ArrayList<>();
        names.addAll(registerMappers(pluginInfo, classLoader));
        names.addAll(registerComponents(pluginInfo, classLoader));
        return names;
    }

    private List<String> registerComponents(PluginInfo pluginInfo, ClassLoader classLoader) throws Exception {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Component.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(Service.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(Repository.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(Controller.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class));

        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        List<String> names = new ArrayList<>();

        for (var candidate : scanner.findCandidateComponents(pluginInfo.getBasePackage())) {
            Class<?> clazz = Class.forName(candidate.getBeanClassName(), true, classLoader);
            if (!isSpringComponent(clazz)) {
                continue;
            }
            String beanName = pluginInfo.getId() + ":" + clazz.getName();
            if (beanFactory.containsSingleton(beanName)) {
                continue;
            }

            Object bean = beanFactory.createBean(clazz);
            beanFactory.registerSingleton(beanName, bean);
            names.add(beanName);

            if (handlerMapping != null && isControllerClass(clazz)) {
                detectHandlerMethods(beanName);
            }
        }
        return names;
    }

    private List<String> registerMappers(PluginInfo pluginInfo, ClassLoader classLoader) throws Exception {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Mapper.class));

        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        List<String> names = new ArrayList<>();

        for (var candidate : scanner.findCandidateComponents(pluginInfo.getBasePackage())) {
            Class<?> mapperType = Class.forName(candidate.getBeanClassName(), true, classLoader);
            if (!mapperType.isInterface()) {
                continue;
            }
            String beanName = pluginInfo.getId() + ":" + mapperType.getName();
            if (beanFactory.containsSingleton(beanName)) {
                continue;
            }
            MapperFactoryBean<?> factoryBean = new MapperFactoryBean<>(mapperType);
            factoryBean.setSqlSessionFactory(applicationContext.getBean(org.apache.ibatis.session.SqlSessionFactory.class));
            factoryBean.afterPropertiesSet();
            beanFactory.registerSingleton(beanName, factoryBean.getObject());
            names.add(beanName);
        }
        return names;
    }

    private boolean isSpringComponent(Class<?> clazz) {
        return AnnotatedElementUtils.hasAnnotation(clazz, Component.class)
                || AnnotatedElementUtils.hasAnnotation(clazz, Service.class)
                || AnnotatedElementUtils.hasAnnotation(clazz, Repository.class)
                || AnnotatedElementUtils.hasAnnotation(clazz, Controller.class)
                || AnnotatedElementUtils.hasAnnotation(clazz, RestController.class);
    }

    private boolean isControllerClass(Class<?> clazz) {
        return AnnotatedElementUtils.hasAnnotation(clazz, Controller.class)
                || AnnotatedElementUtils.hasAnnotation(clazz, RestController.class);
    }

    private void detectHandlerMethods(String beanName) {
        try {
            var method = RequestMappingHandlerMapping.class
                    .getDeclaredMethod("detectHandlerMethods", Object.class);
            method.setAccessible(true);
            method.invoke(handlerMapping, beanName);
        } catch (Exception ignored) {
        }
    }
}

