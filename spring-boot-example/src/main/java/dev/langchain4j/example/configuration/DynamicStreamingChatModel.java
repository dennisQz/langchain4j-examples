package dev.langchain4j.example.configuration;

import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;

import java.util.Map;

public class DynamicStreamingChatModel implements StreamingChatModel {

    private final ModelSelector modelSelector;
    private final Map<String, StreamingChatModel> models;

    public DynamicStreamingChatModel(ModelSelector modelSelector, Map<String, StreamingChatModel> models) {
        this.modelSelector = modelSelector;
        this.models = models;
    }

    private StreamingChatModel getCurrentModel() {
        String currentModelName = modelSelector.getCurrentModel();
        StreamingChatModel model = models.get(currentModelName);
        if (model == null) {
            throw new IllegalStateException("Streaming Model not found: " + currentModelName);
        }
        return model;
    }

    // For newer LangChain4j versions
    @Override
    public void chat(ChatRequest chatRequest, StreamingChatResponseHandler handler) {
        getCurrentModel().chat(chatRequest, handler);
    }
}
