package dev.langchain4j.example.aiservice;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import reactor.core.publisher.Flux;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

@AiService(wiringMode = EXPLICIT, streamingChatModel = "dynamicStreamingChatModel", chatMemoryProvider = "chatMemoryProvider")
public interface StreamingAssistant {

    @SystemMessage("You are a polite assistant")
    Flux<String> chat(@MemoryId String sessionId, @UserMessage String userMessage);
}