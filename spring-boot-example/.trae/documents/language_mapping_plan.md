# 语言编码映射功能实现计划

## 需求分析
将 travel/assistant 接口的 targetLanguage 和 nativeLanguage 参数从语言编码（如 "zh-CN"）转换为语言名称（如 "中文"），再传递给大模型。

## 涉及修改的文件

### 1. 新建语言映射服务
- **文件**: `src/main/java/dev/langchain4j/example/service/LanguageMappingService.java`
- **功能**: 
  - 将语言编码（ISO 639-1 或 BCP 47 格式如 "zh-CN"、"en-US"）转换为语言名称
  - 参考 Google Cloud Translate 文档支持的语言列表
  - 处理常见变体（如 zh-Hans -> 简体中文, zh-Hant -> 繁体中文）

### 2. 修改 TravelController
- **文件**: `src/main/java/dev/langchain4j/example/aiservice/TravelController.java`
- **修改点**:
  - 注入 LanguageMappingService
  - 在填充 prompt 变量之前，将语言编码转换为语言名称
  - 转换后的语言名称用于 prompt 模板变量（targetLanguage, nativeLanguage）
  - 同时也需要更新 ModelSelectionStrategy 的调用（因为它也接收语言参数）

## 实现步骤

### 步骤 1: 创建 LanguageMappingService
- 实现语言编码到语言名称的映射（支持 20+ 常用语言）
- 提供 `String getLanguageName(String languageCode)` 方法
- 处理 null 和未知编码的异常情况

### 步骤 2: 修改 TravelController
- 注入 LanguageMappingService
- 在第 56-58 行填充 prompt 变量之前，先调用映射服务转换语言编码

### 步骤 3: 测试验证
- 使用不同语言编码测试接口（如 zh-CN, en-US, ja, ko 等）
- 验证 prompt 中的语言名称正确传递给大模型

## 语言编码映射示例
| 编码 | 语言名称 |
|------|----------|
| zh, zh-CN, zh-Hans | 中文（简体）|
| zh-TW, zh-Hant | 中文（繁体）|
| en, en-US, en-GB | 英语 |
| ja | 日语 |
| ko | 韩语 |
| fr | 法语 |
| de | 德语 |
| es | 西班牙语 |
| ru | 俄语 |
| ar | 阿拉伯语 |
| ... | ... |

## 预计改动行数
- 新建文件: ~80 行
- 修改 TravelController: ~5 行
