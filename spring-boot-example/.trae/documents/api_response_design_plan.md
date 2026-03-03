# API 接口响应体设计与实现计划

本计划旨在设计并实现统一的 API 响应结构，满足用户对于成功 (200) 和 异常/特殊业务场景 (500) 的响应格式要求。

## 目标

实现以下响应结构：

* **成功**: `{ code: 200, data: { phrases: [], message: '' } }`

* **业务异常/错误**: `{ code: 500, data: { message: '请提供更具体的场景' } }`

## 步骤

1. **创建通用响应类** **`ApiResponse`**

   * 路径: `src/main/java/dev/langchain4j/example/aiservice/common/ApiResponse.java`

   * 包含 `code` (int) 和 `data` (T) 字段。

   * 提供静态方法 `success(T data)` 和 `error(int code, T data)`。

2. **创建全局异常处理器** **`GlobalExceptionHandler`**

   * 路径: `src/main/java/dev/langchain4j/example/aiservice/common/GlobalExceptionHandler.java`

   * 使用 `@RestControllerAdvice` 和 `@ExceptionHandler`。

   * 捕获 `Exception`，返回 `code: 500` 的 `ApiResponse`，其中 `data` 包含错误信息。

3. **修改** **`TravelController`**

   * 路径: `src/main/java/dev/langchain4j/example/aiservice/TravelController.java`

   * 将返回类型从 `TravelResponse` 改为 `ApiResponse<TravelResponse>`。

   * 在 `chat` 方法中调用 `travelAssistant`。

   * **业务逻辑处理**:

     * 检查 `TravelResponse` 是否包含 `message` 且 `phrases` 为空。

     * 如果是（即 AI 提示场景不明确），返回 `code: 500` 的响应。

     * 否则，返回 `code: 200` 的响应。

4. **验证**

   * 使用 `test.http` 或编写简单的测试用例来验证响应格式是否符合预期。

## 详细设计

### ApiResponse 类

```java
public class ApiResponse<T> {
    private int code;
    private T data;
    // ... constructors, getters, static helpers
}
```

### 业务逻辑判断

在 `TravelController` 中：

```java
TravelResponse response = travelAssistant.chat(...);
if (response.getMessage() != null && !response.getMessage().isEmpty() && (response.getPhrases() == null || response.getPhrases().isEmpty())) {
    return ApiResponse.error(500, response);
}
return ApiResponse.success(response);
```

