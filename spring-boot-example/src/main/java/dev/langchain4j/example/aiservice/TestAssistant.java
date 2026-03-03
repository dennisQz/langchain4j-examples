package dev.langchain4j.example.aiservice;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface TestAssistant {

    String chat(@UserMessage("scene") String scene);
}
