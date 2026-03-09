# 启用 LangChain4j 日志输出计划

## 问题分析
用户反馈 `logging.level.dev.langchain4j` 的日志信息未在控制台打印。
经检查，项目当前使用的 Profile 为 `qwen` (`application.properties` 中 `spring.profiles.active=qwen`)。
在 `GlobalModelConfiguration.java` 中，`openaiChatModel` 和 `zhipuChatModel` 均已配置 `.logRequests(true)` 和 `.logResponses(true)`，但 `qwenChatModel` 和 `qwenStreamingChatModel` 缺少此配置。
LangChain4j 的模型通常需要显式开启请求/响应日志，并配合 DEBUG 日志级别才能输出交互详情。

## 实施步骤
1.  修改 `src/main/java/dev/langchain4j/example/configuration/GlobalModelConfiguration.java` 文件。
2.  在 `qwenChatModel` 方法的 `QwenChatModel.builder()` 中添加 `.logRequests(true)` 和 `.logResponses(true)`。
3.  在 `qwenStreamingChatModel` 方法的 `QwenStreamingChatModel.builder()` 中添加 `.logRequests(true)` 和 `.logResponses(true)`。

## 验证
- 代码修改完成后，Qwen 模型的请求和响应日志应能通过 `DEBUG` 级别输出到控制台。
