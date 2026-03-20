package dev.langchain4j.example.config;

public interface ModelSelectionStrategy {

    String selectModel(String targetLanguage, String nativeLanguage);
}
