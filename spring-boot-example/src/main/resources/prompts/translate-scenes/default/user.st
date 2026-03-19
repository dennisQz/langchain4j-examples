请将以下中文短语列表分别翻译成{{targetLanguage}}和{{nativeLanguage}}：

中文短语列表：
{{phrases}}

场景意图列表（需要翻译成{{nativeLanguage}}）：
{{intentions}}

请按照要求的JSON格式输出翻译结果。
要求：
1. **翻译准确性**：确保翻译准确、自然、口语化
3. **结构化输出**：必须返回标准的JSON格式数据，包含一个 'phrases' 列表。
   - 每个元素包含：
     - 'original'：{{nativeLanguage}}翻译
     - 'translated'：{{targetLanguage}}翻译
4. **翻译功能**：同时必须将<>中所有文字翻译为{{nativeLanguage}}并返回翻译结果：
   <Great! Here are 10 useful phrases for [{{scene}}], translated into {{targetLanguage}} with their pronunciation>
   **<>中的内容一定要翻译成{{nativeLanguage}}**, 翻译结果直接作为JSON的顶级字段 'startWords' 返回，不要放在phrases数组中。
5. **意图翻译**：将场景意图列表翻译为{{nativeLanguage}}，翻译结果作为JSON的顶级字段 'intentions' 返回（是一个字符串数组），不要放在phrases数组中。
6. **一定要按照上面要求来执行，不要遗漏步骤**