1. **固定文字翻译**：将固定文字列表翻译为{{nativeLanguage}}，翻译结果作为JSON的顶级字段 'fixedTextList' 返回（是一个字符串数组），不要放在phrases数组中。
2. 请为场景【{{scene}}】生成6条【{{nativeLanguage}}】常用语，并翻译为【{{targetLanguage}}】。
3. **意图推测 (User Intentions)**：
    - 请基于当前场景【{{scene}}】，推测用户在此环境下最可能产生的 3 个具体行为意图或心理诉求。
    - 意图描述应使用【{{nativeLanguage}}】，且表达要简洁、地道（例如：“寻求医疗协助”、“询问商品价格”）。
    - 将结果作为 JSON 顶层字段 `intentions`（字符串数组）返回。
4. **一定要按照上面要求来执行，不要遗漏步骤**

固定文字列表（需要翻译成{{nativeLanguage}}）：
{{fixedTextList}}