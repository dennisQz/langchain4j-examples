package dev.langchain4j.example.aiservice;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

@AiService(wiringMode = EXPLICIT, chatModel = "dynamicChatModel")
public interface TestAssistant {

    String chat(@UserMessage("scene") String scene);
}
