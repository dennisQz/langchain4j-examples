package dev.langchain4j.example.config;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.community.model.zhipu.ZhipuAiChatModel;
import dev.langchain4j.community.model.zhipu.ZhipuAiStreamingChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class GlobalModelConfiguration {

    @Bean
    public ModelSelector modelSelector(@Value("${app.model.current:openai}") String defaultModel) {
        return new ModelSelector(defaultModel);
    }

    @Bean("openaiChatModel")
    public ChatModel openaiChatModel(@Value("${app.model.openai.api-key}") String apiKey,
                                     @Value("${app.model.openai.model-name:gpt-4o-mini}") String modelName,
                                     @Value("${app.model.temperature:0.9}") Double temperature) {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(temperature)
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    @Bean("openaiStreamingChatModel")
    public StreamingChatModel openaiStreamingChatModel(@Value("${app.model.openai.api-key}") String apiKey,
                                                       @Value("${app.model.openai.model-name:gpt-4o-mini}") String modelName,
                                                       @Value("${app.model.temperature:0.9}") Double temperature) {
        return OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(temperature)
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    @Bean("customQwenChatModel")
    public ChatModel qwenChatModel(@Value("${app.model.qwen.api-key}") String apiKey,
                                   @Value("${app.model.qwen.model-name:qwen-turbo}") String modelName,
                                   @Value("${app.model.qwen.max-tokens:2000}") Integer maxTokens,
                                   @Value("${app.model.temperature:0.9}") Double temperature,
                                   ChatModelListener listener) {
        return QwenChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .topP(1.0)
                .maxTokens(maxTokens)
                .temperature(temperature.floatValue())
                .listeners(List.of(listener))
                .build();
    }

    @Bean("customQwenStreamingChatModel")
    public StreamingChatModel qwenStreamingChatModel(@Value("${app.model.qwen.api-key}") String apiKey,
                                                     @Value("${app.model.qwen.model-name:qwen-turbo}") String modelName,
                                                     @Value("${app.model.qwen.max-tokens:2000}") Integer maxTokens,
                                                     @Value("${app.model.temperature:0.9}") Double temperature,
                                                     ChatModelListener listener) {
        return QwenStreamingChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .topP(1.0)
                .maxTokens(maxTokens)
                .temperature(temperature.floatValue())
                .listeners(List.of(listener))
                .build();
    }

    @Bean("zhipuChatModel")
    public ChatModel zhipuChatModel(@Value("${app.model.zhipu.api-key}") String apiKey,
                                    @Value("${app.model.zhipu.model-name:glm-4.7}") String modelName,
                                    @Value("${app.model.zhipu.max-tokens:4096}") Integer maxTokens,
                                    @Value("${app.model.temperature:0.9}") Double temperature) {
        return ZhipuAiChatModel.builder()
                .apiKey(apiKey)
                .model(modelName)
                .maxToken(maxTokens)
                .temperature(temperature)
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    @Bean("zhipuStreamingChatModel")
    public StreamingChatModel zhipuStreamingChatModel(@Value("${app.model.zhipu.api-key}") String apiKey,
                                                      @Value("${app.model.zhipu.model-name:glm-4.7}") String modelName,
                                                      @Value("${app.model.zhipu.max-tokens:4096}") Integer maxTokens,
                                                      @Value("${app.model.temperature:0.9}") Double temperature) {
        return ZhipuAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .model(modelName)
                .maxToken(maxTokens)
                .temperature(temperature)
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    @Bean
    @Primary
    public ChatModel dynamicChatModel(ModelSelector selector,
                                      @Qualifier("openaiChatModel") ChatModel openai,
                                      @Qualifier("customQwenChatModel") ChatModel qwen,
                                      @Qualifier("zhipuChatModel") ChatModel zhipu) {
        Map<String, ChatModel> models = new HashMap<>();
        models.put("openai", openai);
        models.put("qwen", qwen);
        models.put("zhipu", zhipu);
        return new DynamicChatModel(selector, models);
    }

    @Bean
    @Primary
    public StreamingChatModel dynamicStreamingChatModel(ModelSelector selector,
                                                        @Qualifier("openaiStreamingChatModel") StreamingChatModel openai,
                                                        @Qualifier("customQwenStreamingChatModel") StreamingChatModel qwen,
                                                        @Qualifier("zhipuStreamingChatModel") StreamingChatModel zhipu) {
        Map<String, StreamingChatModel> models = new HashMap<>();
        models.put("openai", openai);
        models.put("qwen", qwen);
        models.put("zhipu", zhipu);
        return new DynamicStreamingChatModel(selector, models);
    }
}
