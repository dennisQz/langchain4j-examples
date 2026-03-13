# 计划：将提示词迁移至独立资源文件并支持多模型差异化配置

## 目标
将 `TravelAssistant` 中的硬编码提示词（System Message 和 User Message）迁移到独立的资源文件中（`src/main/resources/prompts/`），并实现根据当前选用的模型（如 OpenAI, Qwen, Zhipu）动态加载对应的提示词。

## 现状分析
1.  **TravelAssistant.java**: 使用 `@SystemMessage` 和 `@UserMessage` 注解硬编码了提示词模板。
2.  **TravelController.java**: 负责接收请求并调用 `TravelAssistant`，根据策略选择模型。
3.  **application.yml**: 包含模型的基本配置，但不适合存放大量长文本提示词。

## 实施步骤

### 1. 创建提示词文件结构
在 `src/main/resources/prompts/travel/` 目录下创建以下文件结构：
- `default/system.st`: 默认的系统提示词
- `default/user.st`: 默认的用户提示词
- `openai/system.st`: OpenAI 专用系统提示词（可选，若无特定需求则复用 default）
- `openai/user.st`: OpenAI 专用用户提示词
- `qwen/system.st`: Qwen 专用系统提示词
- `qwen/user.st`: Qwen 专用用户提示词
- `zhipu/system.st`: Zhipu 专用系统提示词
- `zhipu/user.st`: Zhipu 专用用户提示词

### 2. 创建 PromptManager 服务
创建 `src/main/java/dev/langchain4j/example/service/PromptManager.java`。
- **职责**: 根据 Agent 名称和 Model 名称加载对应的提示词文件。
- **加载逻辑**:
    1. 尝试加载 `classpath:prompts/{agent}/{model}/{type}.st`
    2. 如果文件不存在，回退加载 `classpath:prompts/{agent}/default/{type}.st`
    3. 如果仍不存在，抛出异常。
- **缓存**: 可选，为了性能可以将加载后的内容缓存起来。

### 3. 重构 TravelAssistant 接口
修改 `src/main/java/dev/langchain4j/example/aiservice/TravelAssistant.java`。
- 修改 `chat` 方法签名，移除具体的提示词内容，改为接受动态参数。
- 新签名:
  ```java
  @SystemMessage("{{systemMessage}}")
  @UserMessage("{{userMessage}}")
  TravelResponse chat(@MemoryId String sessionId, @V("systemMessage") String systemMessage, @V("userMessage") String userMessage);
  ```

### 4. 更新 TravelController 逻辑
修改 `src/main/java/dev/langchain4j/example/aiservice/TravelController.java`。
- 注入 `PromptManager`。
- 在 `chat` 方法中：
    1. 获取当前选择的模型名称（如 `openai`, `qwen`）。
    2. 调用 `promptManager.loadPrompt("travel", modelName, PromptType.SYSTEM)` 获取系统提示词模板。
    3. 调用 `promptManager.loadPrompt("travel", modelName, PromptType.USER)` 获取用户提示词模板。
    4. 使用 `dev.langchain4j.model.input.PromptTemplate` 将用户请求参数（`scene`, `nativeLanguage`, `targetLanguage`）填充到 User Message 模板中。
    5. 将填充后的 System Message 和 User Message 传递给 `TravelAssistant`。

## 验证计划
1.  启动应用。
2.  确保 `src/main/resources/prompts/travel/default/` 下的文件被正确加载。
3.  使用 Postman 或 curl 发送请求，指定不同的目标语言（触发不同模型选择）。
4.  验证日志或响应，确保使用了对应模型目录下的提示词（可以通过修改特定模型的提示词来验证）。
