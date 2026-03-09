# 错误分析与修复计划

## 1. 问题分析

**错误现象**：
Spring Boot 应用启动失败，报错 `BeanDefinitionOverrideException`。
具体错误信息为：`Invalid bean definition with name 'qwenChatModel' defined in class path resource [dev/langchain4j/community/dashscope/spring/DashScopeAutoConfiguration.class] ... bound.`

**原因**：
项目引入了 `langchain4j-community-dashscope-spring-boot-starter` 依赖，该依赖包含自动配置类 `DashScopeAutoConfiguration`，它默认定义了一个名为 `qwenChatModel` 的 Bean。
同时，用户在 `GlobalModelConfiguration.java` 中也定义了一个同名的 `@Bean("qwenChatModel")`。
由于 Spring Boot 2.1+ 默认禁止同名 Bean 的覆盖（`spring.main.allow-bean-definition-overriding=false`），导致启动时发生冲突。

## 2. 解决方案

为了解决冲突并保留自定义配置的意图，建议采用以下方案：
**重命名自定义 Bean 名称**：将 `GlobalModelConfiguration` 中定义的 `qwenChatModel` 重命名为 `customQwenChatModel`（或其他不冲突的名称），并在注入处更新 `@Qualifier` 引用。

此方案最安全，避免了覆盖自动配置可能带来的副作用，同时也明确了该 Bean 是用户自定义的。

## 3. 实施步骤

1. **修改** **`GlobalModelConfiguration.java`**：

   * 将 `qwenChatModel` Bean 重命名为 `customQwenChatModel`。

   * 将 `qwenStreamingChatModel` Bean 重命名为 `customQwenStreamingChatModel`（以防流式模型也存在类似冲突）。

   * 更新 `dynamicChatModel` 方法中对 `qwen` 参数的 `@Qualifier` 注解，使其指向新的 Bean 名称。

   * 更新 `dynamicStreamingChatModel` 方法中对 `qwen` 参数的 `@Qualifier` 注解。

2. **验证修复**：

   * 重新启动应用，确认不再报 Bean 定义冲突错误。

