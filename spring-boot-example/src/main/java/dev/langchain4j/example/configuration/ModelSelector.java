package dev.langchain4j.example.configuration;

import java.util.concurrent.atomic.AtomicReference;

public class ModelSelector {
    private final AtomicReference<String> currentModel;

    public ModelSelector(String defaultModel) {
        this.currentModel = new AtomicReference<>(defaultModel);
    }

    public String getCurrentModel() {
        return currentModel.get();
    }

    public void setCurrentModel(String modelName) {
        this.currentModel.set(modelName);
    }
}
