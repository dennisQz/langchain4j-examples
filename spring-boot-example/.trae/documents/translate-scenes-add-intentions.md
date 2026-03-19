# 计划：为 TranslateScenesAssistant 增加 intentions 数组返回功能

## 需求分析

* 在 TranslateScenesAssistant 执行翻译逻辑时，返回 intentions 数组（例如：\['游泳戏水', '晒日光浴', '沙滩运动']）

* intentions 内容需要翻译成 nativeLanguage 对应语言

## 现有代码结构分析

1. **ScenePhraseService** - 已包含 `SceneInfo` 类，其中有 `intentions` 字段 (List<String>)
2. **SceneTranslationResponse** - 当前包含: phrases, message, startWords
3. **TravelResponse** - 最终返回给用户的响应模型
4. **Prompt 模板** (user.st) - 当前只翻译 phrases

## 实现步骤

### 1. 修改 SceneTranslationResponse 模型

* 文件: `src/main/java/dev/langchain4j/example/aiservice/model/SceneTranslationResponse.java`

* 添加字段: `private List<String> intentions;`

* 添加 getter/setter 方法

### 2. 修改 TravelResponse 模型

* 文件: `src/main/java/dev/langchain4j/example/aiservice/model/TravelResponse.java`

* 添加字段: `private List<String> intentions;`

* 添加 getter/setter 方法

### 3. 修改 Prompt 模板

* 文件: `src/main/resources/prompts/translate-scenes/default/user.st`

* 在 JSON 输出要求中添加 intentions 数组翻译指令

* intentions 需要翻译成 nativeLanguage

### 4. 修改 TravelController.handleSceneIdRequest 方法

* 文件: `src/main/java/dev/langchain4j/example/aiservice/TravelController.java`

* 获取 SceneInfo: `scenePhraseService.getSceneBySceneId(sceneId)`

* 提取 intentions: `sceneInfo.getIntentions()`

* 将 intentions 传递给 prompt 变量

* 从 AI 响应中解析 intentions 并设置到 TravelResponse

### 5. 修改 TravelController 将 SceneTranslationResponse 转换为 TravelResponse 的逻辑

* 在转换时同时处理 intentions 字段

## 注意事项

* intentions 翻译成 nativeLanguage（用户母语），不是 targetLanguage（目标语言）

* 确保向后兼容，不影响现有的 phrases 翻译功能

* 需要在 YAML 配置中确认 intentions 数据源（scene-phrases.yml 中已存在）

