### 任务背景
- **当前场景**：{{scene}}
- **用户母语**：{{nativeLanguage}}
- **目标语言**：{{targetLanguage}}

### 执行指令
1. **固定词条翻译**：将下方的“固定文字列表”中的每一项翻译为【{{nativeLanguage}}】，存储在 `fixedTextList` 字段。
2. **常用语生成**：为该场景生成 2 条最实用的【{{nativeLanguage}}】常用语及其对应的【{{targetLanguage}}】翻译，存储在 `phrases` 字段。
3. **意图预测**：基于场景【{{scene}}】，用【{{nativeLanguage}}】推测 3 个用户的潜在行为意图（如：询问位置、寻求帮助），存储在 `intentions` 字段。

### 待处理的固定文字列表
{{fixedTextList}}

### 输出格式要求
必须返回标准的 JSON 格式，严禁包含任何 Markdown 标记或多余文字。结构如下：
{
  "fixedTextList": ["翻译后的文字1", "翻译后的文字2"],
  "phrases": [
    { "original": "母语表达1", "translated": "目标语翻译1" },
    { "original": "母语表达2", "translated": "目标语翻译2" }
  ],
  "intentions": ["意图1", "意图2", "意图3"]
}