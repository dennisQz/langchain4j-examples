package dev.langchain4j.example.configuration;

import dev.langchain4j.community.model.zhipu.ZhipuAiChatModel;
import dev.langchain4j.community.model.zhipu.ZhipuAiStreamingChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("zhipu")
public class ZhipuAiConfiguration {

    @Value("${langchain4j.zhipu-ai.chat-model.api-key}")
    private String apiKey;

    @Value("${langchain4j.zhipu-ai.chat-model.model-name:glm-4}")
    private String modelName;

    @Value("${langchain4j.zhipu-ai.streaming-chat-model.model-name:glm-4}")
    private String streamingModelName;

    @Bean
    ChatModel chatModel() {
        return ZhipuAiChatModel.builder()
                .apiKey(apiKey)
                .model(modelName)
                .build();
    }

    @Bean
    StreamingChatModel streamingChatModel() {
        return ZhipuAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .model(streamingModelName)
                .build();
    }
}
