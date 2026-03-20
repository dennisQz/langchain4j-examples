package dev.langchain4j.example.aiservice;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dev.langchain4j.data.message.*;
import dev.langchain4j.example.config.entity.ChatMessageEntity;
import dev.langchain4j.example.config.repository.ChatMessageRepository;
import dev.langchain4j.example.config.util.SessionIdParser;
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

    private final ChatMessageRepository chatMessageRepository;
    private final ObjectMapper objectMapper;

    public AssistantConfiguration(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
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
            List<ChatMessageEntity> entities = chatMessageRepository.findBySessionIdOrderByMessageOrderAsc(sessionId);

            List<ChatMessage> messages = new ArrayList<>();
            for (ChatMessageEntity entity : entities) {
                try {
                    ChatMessage message = objectMapper.readValue(entity.getMessageJson(), ChatMessage.class);
                    messages.add(message);
                } catch (JsonProcessingException e) {
                    log.warn("Failed to deserialize chat message for session: {}, order: {}, error: {}", 
                        sessionId, entity.getMessageOrder(), e.getMessage());
                }
            }

            if (!messages.isEmpty()) {
                log.info("Loaded {} messages from database for session: {}", messages.size(), sessionId);
            }

            ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(3);
            for (ChatMessage message : messages) {
                chatMemory.add(message);
            }

            return new PersistentChatMemoryWrapper(chatMemory, sessionId, chatMessageRepository, objectMapper, messages.size());
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
        private final ChatMessageRepository repository;
        private final ObjectMapper objectMapper;
        private int lastSavedCount;

        public PersistentChatMemoryWrapper(ChatMemory delegate, String sessionId,
                                           ChatMessageRepository repository, ObjectMapper objectMapper, int initialCount) {
            this.delegate = delegate;
            this.sessionId = sessionId;
            this.repository = repository;
            this.objectMapper = objectMapper;
            this.lastSavedCount = initialCount;
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
            repository.deleteBySessionId(sessionId);
            lastSavedCount = 0;
            log.debug("Cleared chat memory for session: {}", sessionId);
        }

        private void persist() {
            List<ChatMessage> currentMessages = delegate.messages();
            if (currentMessages.size() <= lastSavedCount) {
                return; // Nothing new to save
            }

            SessionIdParser.SessionInfo sessionInfo = SessionIdParser.parse(sessionId);

            for (int i = lastSavedCount; i < currentMessages.size(); i++) {
                ChatMessage message = currentMessages.get(i);
                
                // 跳过系统消息，不存储到数据库
                if (message.type() == ChatMessageType.SYSTEM) {
                    continue;
                }
                try {
                    String json = objectMapper.writeValueAsString(message);
                    ChatMessageEntity entity = new ChatMessageEntity();
                    entity.setSessionId(sessionId);
                    entity.setDeviceId(sessionInfo.deviceId());
                    entity.setSceneId(sessionInfo.sceneId());
                    entity.setMessageJson(json);
                    entity.setMessageType(message.type().name());
                    entity.setMessageOrder(i);
                    
                    repository.save(entity);
                } catch (JsonProcessingException e) {
                    log.error("Failed to serialize chat message for session: {}, order: {}", sessionId, i, e);
                }
            }
            lastSavedCount = currentMessages.size();
            log.debug("Persisted new messages up to order {} for session: {}", lastSavedCount - 1, sessionId);
        }
    }

    @Bean
    ChatMemoryProvider statelessChatMemoryProvider() {
        return memoryId -> {
            String sessionId = String.valueOf(memoryId);
            return new StatelessPersistentChatMemory(sessionId, chatMessageRepository, objectMapper);
        };
    }

    private static class StatelessPersistentChatMemory implements ChatMemory {
        private final String sessionId;
        private final ChatMessageRepository repository;
        private final ObjectMapper objectMapper;
        private final ChatMemory delegate = MessageWindowChatMemory.withMaxMessages(1);

        public StatelessPersistentChatMemory(String sessionId, ChatMessageRepository repository, ObjectMapper objectMapper) {
            this.sessionId = sessionId;
            this.repository = repository;
            this.objectMapper = objectMapper;
        }

        @Override
        public String id() {
            return sessionId;
        }

        @Override
        public List<ChatMessage> messages() {
            return delegate.messages();
        }

        @Override
        public void add(ChatMessage message) {
            delegate.add(message);
            persist(message);
        }

        @Override
        public void clear() {
            delegate.clear();
            // repository.deleteBySessionId(sessionId);
        }

        private void persist(ChatMessage message) {
            try {
                // 跳过系统消息，不存储到数据库
                if (message.type() == ChatMessageType.SYSTEM) {
                    return;
                }
                String json = objectMapper.writeValueAsString(message);
                SessionIdParser.SessionInfo sessionInfo = SessionIdParser.parse(sessionId);

                ChatMessageEntity entity = new ChatMessageEntity();
                entity.setSessionId(sessionId);
                entity.setDeviceId(sessionInfo.deviceId());
                entity.setSceneId(sessionInfo.sceneId());
                entity.setMessageJson(json);
                entity.setMessageType(message.type().name());
                entity.setMessageOrder(repository.countBySessionId(sessionId).intValue());

                repository.save(entity);
                log.debug("Persisted message for session: {}", sessionId);
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize chat message for session: {}", sessionId, e);
            }
        }
    }
}
