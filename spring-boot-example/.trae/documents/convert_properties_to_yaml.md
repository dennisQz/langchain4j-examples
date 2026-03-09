# 计划：支持运行时切换模型及Properties转YAML

本计划旨在将现有的 `properties` 配置文件迁移至 `yaml` 格式，并改造系统以支持在运行时动态切换不同的 LLM 模型（OpenAI, Qwen, Zhipu），而无需重启服务。

## 1. 配置文件迁移

将分散的 `.properties` 文件合并为单一的 `application.yml` 文件。

* 删除 `application.properties`, `application-openai.properties`, `application-qwen.properties`, `application-zhipu.properties`。

* 创建 `src/main/resources/application.yml`。

* 重新组织配置结构，包含所有模型的配置信息。

* 添加自定义配置项 `app.model.current` 用于指定默认启用的模型。

## 2. 模型配置改造

为了支持运行时切换，我们需要手动管理各个模型的 Bean，而不是依赖 Spring Boot Starter 的自动配置（或者在自动配置的基础上增加动态路由）。

* 创建新的配置类 `dev.langchain4j.example.configuration.GlobalModelConfiguration`。

* 在该类中，读取 YAML 中的配置，并分别创建以下 Bean（使用 `@Bean` 注解并指定名称）：

  * `openAiChatModel` / `openAiStreamingChatModel`

  * `qwenChatModel` / `qwenStreamingChatModel`

  * `zhipuChatModel` / `zhipuStreamingChatModel` (替代原有的 `ZhipuAiConfiguration`)

* 移除原有的 `ZhipuAiConfiguration.java`，将其逻辑合并入新配置类。

## 3. 实现动态路由模型

创建一个代理/路由模型，它实现了 `ChatModel` 和 `StreamingChatModel` 接口，并根据当前上下文或全局设置将请求转发给具体的模型实现。

* 创建 `dev.langchain4j.example.configuration.ModelSelector` 类，用于管理当前激活的模型名称（线程安全）。

* 创建 `dev.langchain4j.example.configuration.DynamicChatModel` 类，实现 `ChatModel`。

  * 注入所有具体模型的 Bean（Map\<String, ChatModel>）。

  * 标记为 `@Primary`，确保它被优先注入到业务代码中。

  * 在 `chat()` 方法中，通过 `ModelSelector` 获取当前模型名称，并调用对应模型的 `chat()` 方法。

* 创建 `dev.langchain4j.example.configuration.DynamicStreamingChatModel` 类，实现 `StreamingChatModel`。

  * 逻辑同上，标记为 `@Primary`。

## 4. 提供切换接口

为了在运行时切换模型，需要提供一个 REST API。

* 创建 `dev.langchain4j.example.controller.ModelSwitchController`。

* 提供 `GET /model/current` 查询当前模型。

* 提供 `POST /model/switch?name={modelName}` 切换当前模型。

## 5. 验证与测试

* 启动应用，验证默认模型是否工作。

* 调用切换接口切换到其他模型（如从 Qwen 切换到 Zhipu）。

* 验证切换后，业务接口（如 `/assistant`）是否使用了新的模型。

