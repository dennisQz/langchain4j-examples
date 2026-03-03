package dev.langchain4j.example.aiservice;

import dev.langchain4j.example.lowlevel.ChatModelController;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class AssistantConfiguration {

    /**
     * This chat memory will be used by {@link Assistant} and {@link StreamingAssistant}
     */
    @Bean
    ChatMemoryProvider chatMemoryProvider() {
        Map<Object, ChatMemory> memories = new ConcurrentHashMap<>();
        return memoryId -> memories.computeIfAbsent(memoryId, id -> MessageWindowChatMemory.withMaxMessages(3));
    }

    /**
     * This listener will be injected into every {@link ChatModel} and {@link StreamingChatModel}
     * bean   found in the application context.
     * It will listen for {@link ChatModel} in the {@link ChatModelController} as well as
     * {@link Assistant} and {@link StreamingAssistant}.
     */
    @Bean
    ChatModelListener chatModelListener() {
        return new MyChatModelListener();
    }
}
