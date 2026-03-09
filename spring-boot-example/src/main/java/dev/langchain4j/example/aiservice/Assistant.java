package dev.langchain4j.example.aiservice;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

@AiService(wiringMode = EXPLICIT, chatModel = "dynamicChatModel", chatMemoryProvider = "chatMemoryProvider")
public interface Assistant {

    @SystemMessage("You are a polite assistant")
    String chat(@MemoryId String sessionId, @UserMessage String userMessage);
}