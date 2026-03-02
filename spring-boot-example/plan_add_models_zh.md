# 项目支持千问及智普模型计划

## 1. 目标
在现有的 Spring Boot 项目中添加对千问 (Qwen/DashScope) 和智普 (Zhipu AI/ChatGLM) 模型的支持。

## 2. 现状分析
- 项目基于 `langchain4j-spring-boot-starter`。
- 目前仅在 `pom.xml` 中引入了 `langchain4j-open-ai-spring-boot-starter`。
- 配置文件 `application-qwen.properties` 和 `application-zhipu.properties` 已存在，并包含了相应的配置项。
- `AssistantConfiguration.java` 提供了通用的 `ChatMemory` 和 `ChatModelListener` 配置。

## 3. 实施步骤

### 3.1 修改 `pom.xml`
添加以下依赖：
- `langchain4j-dashscope-spring-boot-starter`: 用于支持千问模型。
- `langchain4j-zhipu-ai-spring-boot-starter`: 用于支持智普模型。

### 3.2 验证配置文件
确认 `application-qwen.properties` 和 `application-zhipu.properties` 中的配置项与新引入的依赖兼容。
- 千问: `langchain4j.dashscope.chat-model.*`
- 智普: `langchain4j.zhipu-ai.chat-model.*`

### 3.3 运行与测试
说明如何通过 Spring Profiles 切换模型：
- 使用 `-Dspring.profiles.active=qwen` 激活千问。
- 使用 `-Dspring.profiles.active=zhipu` 激活智普。
- 提醒设置环境变量 `DASHSCOPE_API_KEY` 和 `ZHIPU_API_KEY`。

## 4. 验收标准
- 项目能成功编译。
- 激活 `qwen` profile 时，能调用千问模型。
- 激活 `zhipu` profile 时，能调用智普模型。
