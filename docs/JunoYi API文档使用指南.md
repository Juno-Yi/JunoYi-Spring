# JunoYi API 文档使用指南

本文档介绍如何在 JunoYi 项目中使用 API 文档功能，基于 SpringDoc OpenAPI 3 + Knife4j UI 实现。

## 1. 访问地址

启动项目后，可以通过以下地址访问 API 文档：

| UI 界面 | 地址 | 说明 |
|--------|------|------|
| Knife4j UI | http://localhost:7588/doc.html | 推荐，界面更友好 |
| Swagger UI | http://localhost:7588/swagger-ui.html | 原生 Swagger 界面 |

## 2. 自动生成 vs 手动注解

### 2.1 自动生成（零配置）

SpringDoc 会自动扫描所有 `@RestController`，根据以下信息自动生成文档：

| 自动识别内容 | 来源 |
|-------------|------|
| 接口路径 | `@RequestMapping`、`@GetMapping` 等 |
| 请求方法 | GET/POST/PUT/DELETE |
| 路径参数 | `@PathVariable` |
| 查询参数 | `@RequestParam` |
| 请求体 | `@RequestBody` |
| 响应类型 | 方法返回值 |
| 字段信息 | DTO/VO 类的字段名和类型 |

**不加任何注解也能生成可用的 API 文档！**

### 2.2 手动注解（可选增强）

如果需要更详细的描述，可以添加注解：

```java
// Controller 分组（可选）
@Tag(name = "用户管理", description = "用户相关接口")
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    // 接口描述（可选）
    @Operation(summary = "获取用户列表", description = "分页查询用户信息")
    @GetMapping("/list")
    public R<PageResult<SysUserVO>> list(SysUserQueryDTO query) {
        return R.ok(userService.list(query));
    }
}
```

```java
// DTO/VO 字段描述（可选）
@Data
public class SysUserVO {
    
    @Schema(description = "用户ID", example = "1")
    private Long id;
    
    @Schema(description = "用户名", example = "admin")
    private String username;
    
    // 不加注解也会显示，只是没有描述
    private String nickname;
}
```

## 3. 配置说明

在 `application.yml` 中配置：

```yaml
junoyi:
  api-doc:
    enable: true                              # 是否启用（生产环境建议关闭）
    title: JunoYi API 文档                     # 文档标题
    description: JunoYi 后台管理系统 API 接口文档
    version: ${junoyi.version}
    contact:
      name: JunoYi
      email: support@junoyi.com
```

## 4. 常用注解速查（可选使用）

| 注解 | 位置 | 说明 |
|------|------|------|
| `@Tag` | Controller 类 | 定义接口分组 |
| `@Operation` | 方法 | 描述接口 |
| `@Parameter` | 参数 | 描述请求参数 |
| `@Schema` | 类/字段 | 描述数据模型 |
| `@Hidden` | 类/方法/字段 | 隐藏不显示 |

## 5. 认证配置

系统已配置全局 JWT Bearer Token 认证：

1. 点击右上角「Authorize」按钮
2. 输入 Token（格式：`Bearer your_token_here`）
3. 点击「Authorize」确认

## 6. 生产环境

建议在生产环境关闭 API 文档：

```yaml
# application-prod.yml
junoyi:
  api-doc:
    enable: false
```

## 7. 总结

- **不加注解**：自动生成基础文档，显示路径、参数、返回值
- **加注解**：增强文档，添加中文描述、示例值等
- **推荐做法**：核心接口加注解，内部接口可以不加
