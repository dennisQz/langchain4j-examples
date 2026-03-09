package dev.langchain4j.example.configuration;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;

import java.util.Map;

public class DynamicChatModel implements ChatModel {

    private final ModelSelector modelSelector;
    private final Map<String, ChatModel> models;

    public DynamicChatModel(ModelSelector modelSelector, Map<String, ChatModel> models) {
        this.modelSelector = modelSelector;
        this.models = models;
    }

    private ChatModel getCurrentModel() {
        String currentModelName = modelSelector.getCurrentModel();
        ChatModel model = models.get(currentModelName);
        if (model == null) {
            throw new IllegalStateException("Model not found: " + currentModelName);
        }
        return model;
    }

    @Override
    public String chat(String userMessage) {
        return getCurrentModel().chat(userMessage);
    }
    
    // For newer LangChain4j versions
    @Override
    public ChatResponse chat(ChatRequest chatRequest) {
        return getCurrentModel().chat(chatRequest);
    }
}
