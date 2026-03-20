如果【{{scene}}】不具体、不明确或是无意义文字或是涉及到任何违反法律法规、不道德或敏感的内容：
    1.使用{{nativeLanguage}}翻译"请询问当前场景相关常用语，如需询问其他场景，请重新添加场景。"，将翻译结果作为JSON的顶级字段 'message' 返回。

如果场景具体明确，则执行以下任务：

1. **固定词条翻译**：
    - 使用{{nativeLanguage}}翻译[{{fixedTextList}}]，将翻译结果作为JSON的顶级字段 'fixedTextList' 返回，不要放在phrases数组中。
2. **生成常用语**：
    - 基于场景【{{scene}}】生成6条【{{nativeLanguage}}】常用语，并翻译为【{{targetLanguage}}】。
    - 生成的常用语对应phrases中original，翻译后的常用语对应phrases中translated
3. **意图推测 (User Intentions)**：
    - 请基于当前场景【{{scene}}】，推测用户在此环境下最可能产生的 3 个具体行为意图或心理诉求。
    - 意图描述应使用【{{nativeLanguage}}】，且表达要简洁、地道（例如：“寻求医疗协助”、“询问商品价格”）。
    - 将结果作为 JSON 顶层字段 `intentions`（字符串数组）返回。

