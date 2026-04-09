# JunoYi Plugin SDK

JunoYi Plugin SDK 用于给插件开发者提供统一开发标准。插件基于该 SDK 开发并打包为 `jar` 后，放入主框架 `plugins/` 目录即可被加载。

## 1. SDK 提供能力

- 插件生命周期接口（`JunoYiPlugin`）
- 插件入口注解与元信息（`@PluginMain`、`PluginInfo`）
- 插件运行上下文（`PluginContext`）
- 统一日志门面（`PluginLogger`）
- 插件事件机制（`PluginEventBus`）
- 与 Spring 容器对接（`ApplicationContext`）

## 2. 推荐插件开发方式

插件可按 SpringBoot 风格组织业务代码，例如：

- Controller
- Service
- Mapper（可使用 MyBatis-Plus）

并在生命周期中通过 `PluginContext` 调用框架能力（日志、事件、Bean）。

## 3. 开发示例

```java
@PluginMain(id = "demo-plugin", name = "Demo Plugin", version = "1.0.0")
public class DemoPlugin implements JunoYiPlugin {

    @Override
    public void onEnable(PluginContext context) {
        context.logger().info("Demo plugin enabled");

        context.eventBus().subscribe(OrderCreatedEvent.class, event ->
                context.logger().info("Order created: " + event.orderId())
        );
    }

    @Override
    public void onDisable(PluginContext context) {
        context.logger().info("Demo plugin disabled");
    }
}
```

## 4. Maven 依赖说明

SDK 依赖默认使用 `provided` 作用域，插件在运行时由主框架提供对应实现：

- Spring Context
- SLF4J API
- MyBatis-Plus Core

## 5. 打包与部署

1. 在插件项目中依赖本 SDK
2. 执行 `mvn clean package` 打包插件
3. 将插件 jar 放入主框架 `plugins/` 目录
4. 启动框架，框架自动扫描并加载插件