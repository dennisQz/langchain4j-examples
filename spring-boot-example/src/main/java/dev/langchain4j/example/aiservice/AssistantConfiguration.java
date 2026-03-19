package dev.langchain4j.example.aiservice;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dev.langchain4j.data.message.*;
import dev.langchain4j.example.configuration.entity.ChatMemoryEntity;
import dev.langchain4j.example.configuration.repository.ChatMemoryRepository;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class AssistantConfiguration {

    private static final Logger log = LoggerFactory.getLogger(AssistantConfiguration.class);

    private final ChatMemoryRepository chatMemoryRepository;
    private final ObjectMapper objectMapper;

    public AssistantConfiguration(ChatMemoryRepository chatMemoryRepository) {
        this.chatMemoryRepository = chatMemoryRepository;
        this.objectMapper = createObjectMapper();
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Allow Jackson to see private fields in ChatMessage implementations
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        // Avoid exception if a bean is empty
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        
        // Register Mixins to use simple "type" field instead of full class names
        mapper.addMixIn(ChatMessage.class, ChatMessageMixin.class);
        mapper.addMixIn(Content.class, ContentMixin.class);
        
        return mapper;
    }

    // Define polymorphic handling for ChatMessage using Mixin
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = SystemMessage.class, name = "system"),
            @JsonSubTypes.Type(value = UserMessage.class, name = "user"),
            @JsonSubTypes.Type(value = AiMessage.class, name = "ai"),
            @JsonSubTypes.Type(value = ToolExecutionResultMessage.class, name = "tool")
    })
    abstract static class ChatMessageMixin {}

    // Define polymorphic handling for Content (used in UserMessage)
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = TextContent.class, name = "text"),
            @JsonSubTypes.Type(value = ImageContent.class, name = "image")
    })
    abstract static class ContentMixin {}

    @Bean
    ChatMemoryProvider chatMemoryProvider() {
        return memoryId -> {
            String sessionId = String.valueOf(memoryId);
            ChatMemoryEntity entity = chatMemoryRepository.findBySessionId(sessionId).orElse(null);

            List<ChatMessage> messages = new ArrayList<>();
            if (entity != null && entity.getChatMemoryJson() != null && !entity.getChatMemoryJson().isEmpty()) {
                try {
                    messages = objectMapper.readValue(entity.getChatMemoryJson(), new TypeReference<List<ChatMessage>>() {});
                    log.info("Loaded {} messages from database for session: {}", messages.size(), sessionId);
                } catch (JsonProcessingException e) {
                    log.warn("Failed to deserialize chat memory for session: {}, will start with empty memory. Error: {}", sessionId, e.getMessage());
                    messages = new ArrayList<>();
                }
            }

            ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(1);
            for (ChatMessage message : messages) {
                chatMemory.add(message);
            }

            return new PersistentChatMemoryWrapper(chatMemory, sessionId, chatMemoryRepository, objectMapper);
        };
    }

    @Bean
    ChatMemoryProvider ephemeralChatMemoryProvider() {
        return memoryId -> MessageWindowChatMemory.withMaxMessages(1);
    }

    @Bean
    ChatModelListener chatModelListener() {
        return new MyChatModelListener();
    }

    private static class PersistentChatMemoryWrapper implements ChatMemory {

        private final ChatMemory delegate;
        private final String sessionId;
        private final ChatMemoryRepository repository;
        private final ObjectMapper objectMapper;

        public PersistentChatMemoryWrapper(ChatMemory delegate, String sessionId,
                                           ChatMemoryRepository repository, ObjectMapper objectMapper) {
            this.delegate = delegate;
            this.sessionId = sessionId;
            this.repository = repository;
            this.objectMapper = objectMapper;
        }

        @Override
        public String id() {
            return sessionId;
        }

        @Override
        public void add(ChatMessage message) {
            delegate.add(message);
            persist();
        }

        @Override
        public List<ChatMessage> messages() {
            return delegate.messages();
        }

        @Override
        public void clear() {
            delegate.clear();
            persist();
        }

        private void persist() {
            try {
                String json = objectMapper.writeValueAsString(delegate.messages());
                ChatMemoryEntity entity = repository.findBySessionId(sessionId)
                        .orElse(new ChatMemoryEntity());
                entity.setSessionId(sessionId);
                entity.setChatMemoryJson(json);
                repository.save(entity);
                log.debug("Persisted chat memory for session: {}", sessionId);
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize chat memory for session: {}", sessionId, e);
            }
        }
    }
}
