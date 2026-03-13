# 场景智能体模型动态路由规划方案

## 1. 任务背景
当前 `TravelAssistant` 智能体通过 `DynamicChatModel` 调用底层大模型，模型选择依赖于 `ModelSelector` 中的全局配置（默认 `qwen`），无法根据请求参数动态切换。
用户需求：根据请求中的 `targetLanguage`（目标语）和 `nativeLanguage`（母语）参数，在请求处理过程中动态决定调用 `qwen`、`zhipu` 或 `openai` 模型。

## 2. 核心思路
1.  **增强 `ModelSelector`**：引入 `ThreadLocal` 机制，使其优先读取当前线程上下文中的模型配置，实现请求级别的模型隔离。
2.  **实现路由策略**：创建模型选择策略服务，封装具体的语言-模型映射逻辑。
3.  **改造控制器**：在 `TravelController` 中集成路由策略，在调用 `TravelAssistant` 前设置上下文，调用后清理上下文。

## 3. 详细实施步骤

### 3.1 增强 ModelSelector (基础设施层)
**目标**：使模型选择器支持线程级上下文覆盖。
**修改文件**：`src/main/java/dev/langchain4j/example/configuration/ModelSelector.java`
**变更内容**：
- 新增 `ThreadLocal<String> contextModel`。
- 修改 `getCurrentModel()` 方法：优先返回 `contextModel` 的值，若为空则返回全局 `currentModel`。
- 新增 `setContextModel(String model)` 和 `clearContextModel()` 方法。

### 3.2 实现模型选择策略 (业务逻辑层)
**目标**：封装根据语言选择模型的业务规则。
**新增文件**：
- 接口：`src/main/java/dev/langchain4j/example/service/ModelSelectionStrategy.java`
- 实现：`src/main/java/dev/langchain4j/example/service/LanguageModelSelectionStrategy.java`
**逻辑规则**（暂定，可调整）：
- 若 `targetLanguage` 或 `nativeLanguage` 包含 "Chinese" 或 "中文"：
  - 优先使用 `qwen` (通义千问) 或 `zhipu` (智谱)。(本方案默认优先 `qwen`，因为它对中文理解较好且是当前默认配置)。
- 其他情况：
  - 默认使用 `openai` (GPT-4o-mini)，以利用其多语言通用能力。
- 兜底：若未匹配到特定规则，返回 `openai`。

### 3.3 改造 TravelController (应用层)
**目标**：在请求生命周期中应用模型选择策略。
**修改文件**：`src/main/java/dev/langchain4j/example/aiservice/TravelController.java`
**变更内容**：
- 注入 `ModelSelectionStrategy` 和 `ModelSelector`。
- 在 `chat` 方法中：
  1. 调用 `strategy.selectModel(target, native)` 获取推荐模型。
  2. 调用 `modelSelector.setContextModel(model)` 设置当前线程模型。
  3. 执行 `travelAssistant.chat(...)`。
  4. 使用 `try-finally` 块确保 `modelSelector.clearContextModel()` 被执行，防止线程污染。

## 4. 风险评估与应对 (针对用户反馈)
**用户关切**：多轮对话中切换模型是否会导致上下文丢失或兼容性问题？

**分析结论**：
1.  **上下文保留 (Memory Persistence)**：
    - `TravelAssistant` 使用独立的 `ChatMemory` (存储在内存或数据库中) 管理会话历史。
    - `ChatMemory` 存储的是通用的 `ChatMessage` 对象，**不绑定特定模型**。
    - 切换模型时，LangChain4j 会将同一 `sessionId` 下的历史消息（包括之前由 Model A 生成的回复）传递给新的 Model B。
    - **结论**：上下文**不会丢失**，新模型能“看到”之前的对话。

2.  **模型兼容性 (Model Compatibility)**：
    - 所有模型（Qwen, Zhipu, OpenAI）均通过统一的 `ChatModel` 接口调用。
    - `TravelAssistant` 定义了严格的 `SystemMessage` 和 JSON 输出格式。主流模型均能较好遵循这些指令。
    - **潜在风险**：不同模型的 Token 计算方式不同。若对话极长且接近 Token 上限，切换到 Context Window 较小的模型可能导致截断（但在本例中 gpt-4o-mini, qwen-turbo, glm-4 均为长窗口模型，风险极低）。

3.  **应对措施**：
    - 确保 `ChatMemory` 配置合理（如使用 `MessageWindowChatMemory` 限制最近 N 条消息），避免历史过长。
    - 在 `TravelAssistant` 中保持 `SystemMessage` 的明确性，确保新模型能快速“进入角色”。

## 5. 验证计划
- 检查代码编译是否通过。
- 确认 `ModelSelector` 的优先级逻辑是否正确。
- 确认 Controller 中 `try-finally` 块的正确使用。
