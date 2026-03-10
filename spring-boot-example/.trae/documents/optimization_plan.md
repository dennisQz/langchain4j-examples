# 智能体回复重复问题分析与优化规划

## 问题分析
用户反馈“同一个用户请求同一个场景，后面的回复返回的数据跟第一次回复一样的，大模型不会返回新常用语”。

主要原因如下：
1.  **模型参数配置 (Temperature)**: 当前 `GlobalModelConfiguration.java` 中未配置 `temperature` 参数。默认情况下，模型倾向于输出概率最高的 token（即“最常用”的短语）。如果不提高温度，对于相同的输入（“餐厅场景”），模型总是倾向于生成完全相同的“最优解”。
2.  **提示词 (Prompt) 限制**: `TravelAssistant` 的 System Message 要求生成“最常用、最实用”的短语。对于特定场景，“最常用”的集合是相对固定的。
3.  **缺乏多样性指令**: 提示词中没有明确指示模型在重复询问时应当“多样化”或“避免重复”。

## 优化方案

### 1. 调整模型参数 (Configuration)
在 `GlobalModelConfiguration.java` 中为各个 ChatModel (OpenAI, Qwen, Zhipu) 显式增加 `temperature` 配置。
-   **目标**: 提高生成的随机性。
-   **数值**: 建议设置为 `0.7` 到 `0.9` 之间（默认通常较低或为 0.7，显式设置可控）。

### 2. 优化提示词 (Prompt)
修改 `TravelAssistant.java` 中的 `@SystemMessage`。
-   **新增指令**: 明确告诉模型，如果上下文中已经存在针对该场景的回答，再次提问时应提供**不同**的短语。
-   **调整措辞**: 将“最常用”调整为“常用且多样化”，或补充“请提供多样化的表达方式”。

## 实施步骤

1.  **修改 `GlobalModelConfiguration.java`**:
    -   为 `OpenAiChatModel`, `QwenChatModel`, `ZhipuAiChatModel` 的 builder 增加 `.temperature(0.8)` (或从配置文件读取)。
    -   为了方便验证，可以直接硬编码或使用 `@Value` 注入（建议使用默认值 0.8）。

2.  **修改 `TravelAssistant.java`**:
    -   更新 `@SystemMessage` 内容，增加关于“多样性”和“处理重复请求”的指令。

3.  **验证**:
    -   (由于无法直接运行交互式测试，将通过代码审查确认变更)。

## 具体修改内容

### GlobalModelConfiguration.java
为每个 Model Builder 添加 `.temperature(0.9)` (提高随机性)。

### TravelAssistant.java
在 System Message 中添加：
> 6. **多样性**：如果用户在同一会话中重复询问相同场景，请务必生成与之前**不同**的短语，以扩展用户的词汇量。
