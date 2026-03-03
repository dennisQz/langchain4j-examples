# 多用户会话隔离支持 (Multi-User Session Isolation Support)

## Why
当前 `Assistant` 服务使用单例 `ChatMemory`，导致所有用户的对话历史混淆在一起。为了支持多用户并发对话，需要通过 `sessionId` 对用户会话进行隔离。

## What Changes
- 修改 `Assistant`、`StreamingAssistant` 和 `TravelAssistant` 接口，增加 `@MemoryId` 参数。
- 修改 `AssistantConfiguration`，将 `ChatMemory` Bean 替换为 `ChatMemoryProvider` Bean。
- 修改 `AssistantController` 和 `TravelController`，在请求中接收 `sessionId`（或 `clientId`），并传递给 AI 服务。

## Impact
- **Affected specs**: `Assistant`, `StreamingAssistant`, `TravelAssistant` 接口签名变更。
- **Affected code**: 
    - `AssistantConfiguration.java`
    - `Assistant.java`
    - `StreamingAssistant.java`
    - `TravelAssistant.java`
    - `AssistantController.java`
    - `TravelController.java`
    - `TestAssistant.java`
    - `TravelControllerTest.java`

## ADDED Requirements
### Requirement: Multi-User Session Isolation
系统必须能够区分不同用户的对话历史。
- **WHEN** 用户 A 发送消息 "Hello" 且 sessionId="userA"
- **AND** 用户 B 发送消息 "Hi" 且 sessionId="userB"
- **THEN** 用户 A 的后续请求只能看到 "Hello" 相关的历史，用户 B 的后续请求只能看到 "Hi" 相关的历史。

## MODIFIED Requirements
### Requirement: Assistant Service
`chat` 方法必须接受 `sessionId` 参数。
- 旧: `chat(String userMessage)`
- 新: `chat(@MemoryId String sessionId, String userMessage)`

### Requirement: Assistant Controller
API 端点必须支持 `sessionId` 参数（可选，默认值为 "default"）。
- `GET /assistant?message=...&sessionId=...`
- `GET /streamingAssistant?message=...&sessionId=...`
