# 实现计划：Travel Assistant SceneId 支持及翻译功能增强

## 需求分析

### 背景

现有 `/travel/assistant` 接口已经支持根据场景生成翻译短语，但用户希望增加以下功能：

1. 根据 `sceneId` 参数从 `scene-phrases.yml` 文件中匹配对应场景
2. 将10条短语从中文翻译为 `nativeLanguage` 和 `targetLanguage`
3. 返回结构化JSON，包含 `default`、`original`、`translated` 字段
4. 评估是否需要新增翻译智能体

### 现有代码结构

* **TravelController.java**: 处理 `/travel/assistant` 请求

* **TravelRequest.java**: 包含 `scene`、`sceneId`、`targetLanguage`、`nativeLanguage` 字段

* **TravelResponse.java**: 包含 `phrases` 列表

* **TravelPhrase.java**: 包含 `original`、`translated`、`phonetic` 字段

* **scene-phrases.yml**: 包含12个场景的中文短语（每个场景10条）

* **AssistantConfiguration.java**: 包含 ChatMemoryProvider，实现持久化到数据库

***

## 实现计划

### 步骤1：修改 TravelPhrase 模型类

* 添加 `default` 字段，存储原始中文短语

* 修改构造函数和getter/setter

**修改文件**: `src/main/java/dev/langchain4j/example/aiservice/model/TravelPhrase.java`

### 步骤2：创建 YAML 解析服务

* 创建 `ScenePhraseService` 服务类

* 从 `scene-phrases.yml` 读取短语

* 根据 `sceneId` 返回对应的短语列表

**新建文件**: `src/main/java/dev/langchain4j/example/service/ScenePhraseService.java`

### 步骤3：创建翻译智能体（TranslateScenesAssistant）

* 创建 `TranslateScenesAssistant` 接口，使用 LangChain4j AI Service

* 专门用于翻译已存在的短语

* **重要**：需要创建一个非持久化的 ChatMemoryProvider

**新建文件**:

* `src/main/java/dev/langchain4j/example/aiservice/TranslateScenesAssistant.java`

### 步骤4：创建非持久化的 ChatMemoryProvider

* 创建 `EphemeralChatMemoryProvider` Bean

* 只在内存中存储对话，不持久化到数据库

* 用于 TranslateScenesAssistant

**修改文件**: `src/main/java/dev/langchain4j/example/aiservice/AssistantConfiguration.java`

### 步骤5：创建翻译用的 Prompt

* 创建 `prompts/translate-scenes/default/system.st`

* 创建 `prompts/translate-scenes/default/user.st`

**新建文件**:

* `src/main/resources/prompts/translate-scenes/default/system.st`

* `src/main/resources/prompts/translate-scenes/default/user.st`

### 步骤6：修改 TravelController 逻辑

* 判断请求是否包含 `sceneId`

* 如果有 `sceneId`：

  1. 调用 `ScenePhraseService` 获取中文短语列表
  2. 调用 `TranslateScenesAssistant` 翻译为目标语言
  3. **关键**：调用完成后立即清除上下文
  4. 组装响应（包含 default、original、translated）

* 如果没有 `sceneId`：保持原有逻辑不变

**修改文件**: `src/main/java/dev/langchain4j/example/aiservice/TravelController.java`

### 步骤7：更新测试

* 添加针对 `sceneId` 场景的单元测试

* 测试 YAML 解析和翻译功能

* 测试上下文清除逻辑

**修改文件**: `src/test/java/dev/langchain4j/example/aiservice/TravelControllerTest.java`

***

## 关于上下文管理的详细说明

### 问题背景

当前 `ChatMemoryProvider` 实现（`PersistentChatMemoryWrapper`）会将每次对话保存到数据库。用户要求 TranslateScenesAssistant：

1. 执行完后清空上下文
2. 上下文不会存储到数据库

### 解决方案

#### 方案：创建非持久化的 ChatMemoryProvider

在 `AssistantConfiguration.java` 中新增一个 Bean：

```java
@Bean
ChatMemoryProvider ephemeralChatMemoryProvider() {
    return memoryId -> MessageWindowChatMemory.withMaxMessages(1);
}
```

#### TranslateScenesAssistant 配置

在创建 TranslateScenesAssistant 时，使用 `ephemeralChatMemoryProvider` 而不是默认的 `chatMemoryProvider`：

```java
@AiService(
    wiringMode = EXPLICIT, 
    chatModel = "dynamicChatModel", 
    chatMemoryProvider = "ephemeralChatMemoryProvider"  // 使用非持久化
)
public interface TranslateScenesAssistant {
    // ...
}
```

#### TravelController 中的上下文清除逻辑

```java
@PostMapping("/travel/assistant")
public ApiResponse<TravelResponse> chat(@RequestBody TravelRequest request) {
    String sessionId = request.getSessionId() != null ? request.getSessionId() : "default";

    // Select model based on strategy
    String selectedModel = modelSelectionStrategy.selectModel(request.getTargetLanguage(), request.getNativeLanguage());
    modelSelector.setContextModel(selectedModel);

    try {
        // 判断是否有 sceneId
        if (request.getSceneId() != null && !request.getSceneId().isEmpty()) {
            // sceneId 路径：使用 YAML + 翻译智能体
            return handleSceneIdRequest(request, selectedModel, sessionId);
        } else {
            // 原有逻辑：生成场景短语
            return handleGenerateRequest(request, selectedModel, sessionId);
        }
    } finally {
        modelSelector.clearContextModel();
    }
}

private ApiResponse<TravelResponse> handleSceneIdRequest(TravelRequest request, String selectedModel, String sessionId) {
    // 1. 获取中文短语列表
    List<String> chinesePhrases = scenePhraseService.getPhrasesBySceneId(request.getSceneId());
    
    // 2. 转换语言代码为语言名称
    String targetLanguageName = languageMappingService.getLanguageName(request.getTargetLanguage());
    String nativeLanguageName = languageMappingService.getLanguageName(request.getNativeLanguage());
    
    // 3. 构建 prompt
    // ... 构建 systemPrompt 和 userPrompt ...
    
    // 4. 调用翻译智能体（使用独立的 sessionId 避免混淆）
    String translationSessionId = sessionId + "_translate";
    TravelResponse response = translateScenesAssistant.translate(translationSessionId, systemPrompt, userPrompt);
    
    // 5. 关键：翻译完成后立即清除上下文（双重保障）
    ephemeralChatMemoryProvider.get(translationSessionId).clear();
    
    return ApiResponse.success(response);
}
```

***

## 是否需要新增翻译智能体？

### 分析

**建议：需要新增翻译智能体（TranslateScenesAssistant）**

原因：

1. **职责分离**：当前 TravelAssistant 负责生成场景短语并翻译，新增功能是翻译预定义的短语，职责不同
2. **Prompt 差异**：翻译预定义短语和生成新短语的 Prompt 模板不同
3. **模型选择**：翻译任务和生成任务可能需要不同的模型配置
4. **可复用性**：TranslateScenesAssistant 可以被其他功能复用

***

## 总结

| 任务                       | 类型 | 文件                            |
| ------------------------ | -- | ----------------------------- |
| 添加 default 字段            | 修改 | TravelPhrase.java             |
| 创建场景短语服务                 | 新建 | ScenePhraseService.java       |
| 创建翻译智能体                  | 新建 | TranslateScenesAssistant.java |
| 创建非持久化ChatMemoryProvider | 修改 | AssistantConfiguration.java   |
| 创建翻译 Prompt              | 新建 | prompts/translate-scenes/\*   |
| 修改控制器逻辑                  | 修改 | TravelController.java         |
| 更新测试                     | 修改 | TravelControllerTest.java     |

