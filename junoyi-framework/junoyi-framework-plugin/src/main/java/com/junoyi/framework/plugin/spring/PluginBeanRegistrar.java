package com.junoyi.framework.plugin.spring;

import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.plugin.domain.PluginInfo;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
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

    private final JunoYiLog log = JunoYiLogFactory.getLogger(PluginBeanRegistrar.class);
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
        ClassPathScanningCandidateComponentProvider scanner = createScanner(classLoader);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Component.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(Service.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(Repository.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(Controller.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class));

        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        List<String> names = new ArrayList<>();
        List<Class<?>> componentClasses = new ArrayList<>();

        for (var candidate : scanner.findCandidateComponents(pluginInfo.getBasePackage())) {
            Class<?> clazz = Class.forName(candidate.getBeanClassName(), true, classLoader);
            if (isSpringComponent(clazz)) {
                componentClasses.add(clazz);
            }
        }

        // 先注册 Service/Repository/Component，再注册 Controller，避免构造器依赖找不到
        registerComponentPhase(componentClasses, false, pluginInfo, classLoader, beanFactory, names);
        registerComponentPhase(componentClasses, true, pluginInfo, classLoader, beanFactory, names);

        return names;
    }

    private void registerComponentPhase(List<Class<?>> componentClasses,
                                        boolean controllerOnly,
                                        PluginInfo pluginInfo,
                                        ClassLoader classLoader,
                                        ConfigurableListableBeanFactory beanFactory,
                                        List<String> names) {
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
        for (Class<?> clazz : componentClasses) {
            if (isControllerClass(clazz) != controllerOnly) {
                continue;
            }
            String beanName = pluginInfo.getName() + ":" + clazz.getName();
            if (beanFactory.containsSingleton(beanName) || registry.containsBeanDefinition(beanName)) {
                continue;
            }

            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            beanDefinition.setBeanClass(clazz);
            beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
            registry.registerBeanDefinition(beanName, beanDefinition);

            Object bean = getBeanWithClassLoader(beanFactory, beanName, classLoader);
            names.add(beanName);

            if (handlerMapping != null && controllerOnly) {
                detectHandlerMethods(beanName, bean);
            }
        }
    }

    private Object getBeanWithClassLoader(ConfigurableListableBeanFactory beanFactory,
                                          String beanName,
                                          ClassLoader classLoader) {
        Thread currentThread = Thread.currentThread();
        ClassLoader originalContextClassLoader = currentThread.getContextClassLoader();
        ClassLoader originalBeanClassLoader = beanFactory.getBeanClassLoader();
        try {
            currentThread.setContextClassLoader(classLoader);
            beanFactory.setBeanClassLoader(classLoader);
            return applicationContext.getBean(beanName);
        } finally {
            beanFactory.setBeanClassLoader(originalBeanClassLoader);
            currentThread.setContextClassLoader(originalContextClassLoader);
        }
    }

    private List<String> registerMappers(PluginInfo pluginInfo, ClassLoader classLoader) throws Exception {
        ClassPathScanningCandidateComponentProvider scanner = createScanner(classLoader);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Mapper.class));

        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        List<String> names = new ArrayList<>();

        for (var candidate : scanner.findCandidateComponents(pluginInfo.getBasePackage())) {
            Class<?> mapperType = Class.forName(candidate.getBeanClassName(), true, classLoader);
            if (!mapperType.isInterface()) {
                continue;
            }
            String beanName = pluginInfo.getName() + ":" + mapperType.getName();
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

    private ClassPathScanningCandidateComponentProvider createScanner(ClassLoader classLoader) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.setResourceLoader(new PathMatchingResourcePatternResolver(classLoader));
        return scanner;
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

    private void detectHandlerMethods(String beanName, Object bean) {
        try {
            var method = findDetectHandlerMethods();
            // 先尝试 beanName（与 Spring 默认逻辑保持一致）
            method.invoke(handlerMapping, beanName);
            return;
        } catch (Exception e) {
            log.warn("Plugin", "Detect handler by beanName failed: {}", beanName);
        }

        try {
            var method = findDetectHandlerMethods();
            // 回退为直接传入 bean 实例，避免名称解析问题导致路由未注册
            method.invoke(handlerMapping, bean);
        } catch (Exception e) {
            log.error("Plugin", "Detect handler methods failed for bean: " + beanName, e);
        }
    }

    private java.lang.reflect.Method findDetectHandlerMethods() throws NoSuchMethodException {
        Class<?> type = handlerMapping.getClass();
        while (type != null) {
            try {
                var method = type.getDeclaredMethod("detectHandlerMethods", Object.class);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException ignored) {
                type = type.getSuperclass();
            }
        }
        throw new NoSuchMethodException("detectHandlerMethods(Object)");
    }
}

