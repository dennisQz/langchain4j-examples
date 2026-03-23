# SessionId MD5 解码计划

## 需求说明
在 `TravelController` 的 `/travel/assistant` 接口中，将请求参数 `sessionId` 进行 MD5 处理。

## 技术说明
MD5 是单向哈希算法，无法直接"解码"。本计划采用 **MD5 哈希** 方式将传入的 sessionId 转换为哈希值，用于后续处理。

## 实现步骤

### 1. 创建 MD5 工具类
- 创建 `dev.langchain4j.example.config.util.Md5Util` 工具类
- 提供 `hash(String input)` 方法，返回 32 位小写 MD5 哈希值
- 使用 Spring Boot 内置的 `DigestUtils` 或 Java `MessageDigest`

### 2. 修改 TravelController
- 在 `chat` 方法中，接收到 `sessionId` 后调用 `Md5Util` 进行哈希处理
- 将哈希后的值作为新的 sessionId 使用
- 确保原有业务逻辑不变

### 3. 验证测试
- 启动应用测试接口，确认 MD5 哈希功能正常工作

## 涉及文件
- 新建：`src/main/java/dev/langchain4j/example/config/util/Md5Util.java`
- 修改：`src/main/java/dev/langchain4j/example/controller/TravelController.java`
