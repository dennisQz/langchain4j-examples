# Tasks

- [x] Task 1: 定义数据模型
  - 创建 `TravelRequest` 类（包含 `scene`, `nativeLanguage` 字段）。
  - 创建 `TravelPhrase` 类（包含 `original`, `translated` 等字段）。
  - 创建 `TravelResponse` 类（包含 `phrases` 列表）。
- [x] Task 2: 创建 `TravelAssistant` 接口
  - 定义 `@SystemMessage`，包含角色设定、合规性要求、长度限制和 JSON 输出要求。
  - 定义 `@UserMessage` 方法，接收 `scene` 和 `nativeLanguage`。
  - 配置返回类型为 `TravelResponse` 或 `String`（如果使用 JSON 模式）。
- [x] Task 3: 实现 `TravelController`
  - 创建 REST 控制器。
  - 添加 `POST /travel/assistant` 端点。
  - 接收 `TravelRequest` 对象。
  - 调用 `TravelAssistant` 并返回结果。
- [x] Task 4: 验证与测试
  - 启动应用并测试接口。
  - 验证 JSON 格式。
  - 验证内容数量（10条）和相关性。
