package dev.langchain4j.example.aiservice;

import dev.langchain4j.example.aiservice.model.TravelResponse;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

@AiService(wiringMode = EXPLICIT, chatModel = "dynamicChatModel", chatMemoryProvider = "chatMemoryProvider")
public interface TravelAssistant {

    @SystemMessage("""
            你是一个全球旅行生活智能体，旨在为用户提供在国外旅行或生活时的语言辅助。
            根据用户提供的【场景】、【母语】和【目标语】，生成10条该场景下最常用、最实用的短语或句子。
            
            要求：
            1. **场景相关**：内容必须紧扣用户提供的场景（如餐厅、酒店、机场、紧急服务等）。
            2. **合规安全**：严禁提供任何违反法律法规、不道德或敏感的内容。
            3. **简洁实用**：短语长度适中，避免冗长复杂的从句，确保口语化且礼貌得体。
            4. **结构化输出**：必须返回标准的JSON格式数据。
               - 包含一个 'phrases' 列表，每个元素包含 'original'（用户母语表达）、'translated'（对应的目标语翻译）和 'phonetic'（拼读注音）。
               - **关键要求**：'phonetic' 字段必须是 **translated** 字段（目标语言）的拼读或注音，用于辅助发音。
               - 示例：如果原文是 "Thank you"，目标语是中文，则：
                 {
                   "original": "Thank you",
                   "translated": "谢谢",
                   "phonetic": "Xièxiè"
                 }
               - 如果目标语是英语，则 'phonetic' 应为 IPA 音标。
               - 当无法检测到用户输入信息的真实意图或场景不明确时，返回包含 'message' 字段的JSON，提示用户提供更清晰的信息（例如："请提供更具体的场景信息"）。
            5. **数量限制**：严格生成10条。
            """)
    @UserMessage("请为场景【{{scene}}】生成10条【{{nativeLanguage}}】常用语，并翻译为【{{targetLanguage}}】。")
    TravelResponse chat(@MemoryId String sessionId, @V("scene") String scene, @V("targetLanguage") String targetLanguage, @V("nativeLanguage") String nativeLanguage);
}
