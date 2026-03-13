package dev.langchain4j.example.configuration;

import java.util.concurrent.atomic.AtomicReference;

public class ModelSelector {
    private final AtomicReference<String> currentModel;
    private static final ThreadLocal<String> contextModel = new ThreadLocal<>();

    public ModelSelector(String defaultModel) {
        this.currentModel = new AtomicReference<>(defaultModel);
    }

    public String getCurrentModel() {
        String local = contextModel.get();
        if (local != null) {
            return local;
        }
        return currentModel.get();
    }

    public void setCurrentModel(String modelName) {
        this.currentModel.set(modelName);
    }

    public void setContextModel(String modelName) {
        contextModel.set(modelName);
    }

    public void clearContextModel() {
        contextModel.remove();
    }
}
