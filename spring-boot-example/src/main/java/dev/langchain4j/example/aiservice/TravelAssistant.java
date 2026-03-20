package dev.langchain4j.example.aiservice;

import dev.langchain4j.example.aiservice.model.TravelResponse;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

@AiService(wiringMode = EXPLICIT, chatModel = "dynamicChatModel", chatMemoryProvider = "statelessChatMemoryProvider")
public interface TravelAssistant {

    @SystemMessage("{{systemMessage}}")
    @UserMessage("{{userMessage}}")
    TravelResponse chat(@MemoryId String sessionId, @V("systemMessage") String systemMessage, @V("userMessage") String userMessage);
}
