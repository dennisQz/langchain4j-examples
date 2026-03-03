# 全球旅行生活智能体项目 Spec

## Why
为了帮助旅行者在不同国家和场景下顺畅沟通，我们需要将现有的 AI 助手改造为一个“全球旅行生活智能体”。该智能体能够根据用户提供的特定场景（如餐厅、酒店、机场等）和母语，动态生成实用、合规且简洁的咨询常用语。

## What Changes
- **新增 API 接口**: `POST /travel/assistant`
  - **输入**:
    - JSON Body: `{ "scene": "场景名称", "nativeLanguage": "母语" }`
  - **输出**: 标准 JSON 格式，包含 10 条常用语。
- **新增 AI 服务接口**: `TravelAssistant`
  - 使用 `@SystemMessage` 定义角色和约束（合规性、简洁性、JSON 格式）。
  - 使用 `@UserMessage` 接收场景和语言参数。
- **数据结构**: 定义 `TravelResponse` 和 `TravelPhrase` POJO 类，用于结构化输出（如果 LLM 支持）或 JSON 解析。

## Impact
- **Affected Specs**: 新增旅行助手能力。
- **Affected Code**:
  - 新增 `src/main/java/dev/langchain4j/example/aiservice/TravelAssistant.java`
  - 新增 `src/main/java/dev/langchain4j/example/aiservice/TravelController.java`
  - 新增 `src/main/java/dev/langchain4j/example/aiservice/TravelResponse.java` (可选，视实现方式而定)

## ADDED Requirements
### Requirement: 场景化常用语生成
系统应提供一个接口，根据场景和母语生成常用语。

#### Scenario: 成功生成
- **WHEN** 用户请求 `/travel/assistant?scene=Restaurant&nativeLanguage=Chinese`
- **THEN** 返回包含 10 条餐厅相关常用语的 JSON，每条常用语包含原文（如当地语言）和母语翻译。

### Requirement: 内容合规性
生成的内容必须符合当地法律法规，避免任何非法建议。

### Requirement: 简洁性
生成的常用语应简短易懂，方便用户快速阅读和使用。

### Requirement: JSON 输出
输出必须为标准 JSON 格式，便于前端解析和展示。
