# 字典 API 使用指南

## 概述

字典 API (`SysDictApi`) 提供了一组供其他模块调用的字典服务接口，用于在业务代码中查询和使用字典数据。

## API 接口位置

- **接口定义**: `junoyi-module-api/junoyi-module-system-api` 模块
  - 包路径: `com.junoyi.system.api.SysDictApi`
  
- **接口实现**: `junoyi-module/junoyi-module-system` 模块
  - 包路径: `com.junoyi.system.api.SysDictApiImpl`

## 依赖配置

在需要使用字典 API 的模块中，添加以下依赖：

```xml
<dependency>
    <groupId>com.junoyi</groupId>
    <artifactId>junoyi-module-system-api</artifactId>
    <version>${project.version}</version>
</dependency>
```

## API 方法说明

### 1. 根据字典类型查询字典数据

```java
List<SysDictDataVO> getDictDataByType(String dictType)
```

**功能**: 查询指定字典类型下的所有启用状态的字典数据，按排序字段升序排列。

**参数**:
- `dictType`: 字典类型

**返回**: 字典数据列表

**使用示例**:
```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final SysDictApi sysDictApi;
    
    public void processOrder() {
        // 获取订单状态字典
        List<SysDictDataVO> orderStatusList = sysDictApi.getDictDataByType("order_status");
        
        for (SysDictDataVO dictData : orderStatusList) {
            System.out.println("标签: " + dictData.getDictLabel() + ", 值: " + dictData.getDictValue());
        }
    }
}
```

---

### 2. 根据字典类型和值获取标签

```java
String getDictLabel(String dictType, String dictValue)
```

**功能**: 根据字典类型和字典值获取对应的字典标签（用于显示）。

**参数**:
- `dictType`: 字典类型
- `dictValue`: 字典值

**返回**: 字典标签，如果不存在返回 `null`

**使用示例**:
```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final SysDictApi sysDictApi;
    
    public String getOrderStatusLabel(String statusValue) {
        // 将状态值转换为显示标签
        // 例如: "1" -> "待支付", "2" -> "已支付"
        return sysDictApi.getDictLabel("order_status", statusValue);
    }
}
```

---

### 3. 根据字典类型和标签获取值

```java
String getDictValue(String dictType, String dictLabel)
```

**功能**: 根据字典类型和字典标签获取对应的字典值（用于存储）。

**参数**:
- `dictType`: 字典类型
- `dictLabel`: 字典标签

**返回**: 字典值，如果不存在返回 `null`

**使用示例**:
```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final SysDictApi sysDictApi;
    
    public String getOrderStatusValue(String statusLabel) {
        // 将显示标签转换为状态值
        // 例如: "待支付" -> "1", "已支付" -> "2"
        return sysDictApi.getDictValue("order_status", statusLabel);
    }
}
```

---

### 4. 检查字典数据是否存在

```java
boolean existsDictData(String dictType, String dictValue)
```

**功能**: 检查指定字典类型下是否存在某个字典值。

**参数**:
- `dictType`: 字典类型
- `dictValue`: 字典值

**返回**: `true`-存在，`false`-不存在

**使用示例**:
```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final SysDictApi sysDictApi;
    
    public void validateOrderStatus(String status) {
        // 验证订单状态是否有效
        if (!sysDictApi.existsDictData("order_status", status)) {
            throw new IllegalArgumentException("无效的订单状态: " + status);
        }
    }
}
```

---

### 5. 批量查询字典数据

```java
Map<String, List<SysDictDataVO>> getDictDataByTypes(List<String> dictTypes)
```

**功能**: 批量查询多个字典类型的数据，返回以字典类型为 key 的 Map。

**参数**:
- `dictTypes`: 字典类型列表

**返回**: 字典类型为 key，字典数据列表为 value 的 Map

**使用示例**:
```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final SysDictApi sysDictApi;
    
    public Map<String, List<SysDictDataVO>> getOrderDictData() {
        // 批量获取订单相关的所有字典
        List<String> dictTypes = List.of("order_status", "payment_method", "delivery_method");
        return sysDictApi.getDictDataByTypes(dictTypes);
    }
    
    public void displayOrderInfo() {
        Map<String, List<SysDictDataVO>> dictMap = getOrderDictData();
        
        // 获取订单状态字典
        List<SysDictDataVO> orderStatusList = dictMap.get("order_status");
        
        // 获取支付方式字典
        List<SysDictDataVO> paymentMethodList = dictMap.get("payment_method");
        
        // 获取配送方式字典
        List<SysDictDataVO> deliveryMethodList = dictMap.get("delivery_method");
    }
}
```

## 完整使用示例

```java
package com.junoyi.order.service.impl;

import com.junoyi.system.api.SysDictApi;
import com.junoyi.system.domain.vo.SysDictDataVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 订单服务实现类
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl {
    
    private final SysDictApi sysDictApi;
    
    /**
     * 获取订单状态显示文本
     */
    public String getOrderStatusText(String statusValue) {
        String label = sysDictApi.getDictLabel("order_status", statusValue);
        return label != null ? label : "未知状态";
    }
    
    /**
     * 验证订单状态是否有效
     */
    public boolean isValidOrderStatus(String status) {
        return sysDictApi.existsDictData("order_status", status);
    }
    
    /**
     * 获取所有可用的订单状态
     */
    public List<SysDictDataVO> getAllOrderStatus() {
        return sysDictApi.getDictDataByType("order_status");
    }
    
    /**
     * 初始化订单相关字典数据
     */
    public void initOrderDictData() {
        // 批量获取订单相关字典
        List<String> dictTypes = List.of(
            "order_status",      // 订单状态
            "payment_method",    // 支付方式
            "delivery_method",   // 配送方式
            "order_source"       // 订单来源
        );
        
        Map<String, List<SysDictDataVO>> dictMap = sysDictApi.getDictDataByTypes(dictTypes);
        
        // 处理字典数据...
        dictMap.forEach((type, dataList) -> {
            System.out.println("字典类型: " + type);
            dataList.forEach(data -> {
                System.out.println("  - " + data.getDictLabel() + ": " + data.getDictValue());
            });
        });
    }
}
```

## 注意事项

1. **空值处理**: 所有方法都会对空参数进行校验，返回空集合或 `null`，不会抛出异常。

2. **状态过滤**: 所有查询方法只返回状态为"0"（正常）的字典数据，停用的字典数据不会被查询到。

3. **排序规则**: `getDictDataByType` 和 `getDictDataByTypes` 方法返回的数据按 `dictSort` 字段升序排列。

4. **性能考虑**: 
   - 对于频繁使用的字典数据，建议在应用启动时缓存到内存中
   - 批量查询时使用 `getDictDataByTypes` 方法，避免多次数据库查询

5. **依赖注入**: 使用 `@RequiredArgsConstructor` 或 `@Autowired` 注入 `SysDictApi` 接口即可使用。

## 常见使用场景

### 场景1: 下拉框数据源

```java
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    
    private final SysDictApi sysDictApi;
    
    @GetMapping("/status/options")
    public R<List<SysDictDataVO>> getOrderStatusOptions() {
        // 为前端下拉框提供数据源
        return R.ok(sysDictApi.getDictDataByType("order_status"));
    }
}
```

### 场景2: 数据转换显示

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final SysDictApi sysDictApi;
    
    public OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        vo.setOrderId(order.getOrderId());
        
        // 将状态值转换为显示文本
        vo.setStatusText(sysDictApi.getDictLabel("order_status", order.getStatus()));
        
        return vo;
    }
}
```

### 场景3: 数据验证

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final SysDictApi sysDictApi;
    
    public void createOrder(OrderDTO dto) {
        // 验证订单状态是否有效
        if (!sysDictApi.existsDictData("order_status", dto.getStatus())) {
            throw new BusinessException("无效的订单状态");
        }
        
        // 验证支付方式是否有效
        if (!sysDictApi.existsDictData("payment_method", dto.getPaymentMethod())) {
            throw new BusinessException("无效的支付方式");
        }
        
        // 创建订单...
    }
}
```

## 总结

字典 API 提供了一套简洁、易用的接口，用于在业务代码中查询和使用字典数据。通过这些接口，可以实现：

- 字典数据的查询和转换
- 数据有效性验证
- 前端下拉框数据源
- 批量字典数据获取

合理使用这些 API 可以提高代码的可维护性和可读性。
