package dev.langchain4j.example.service;

/**
 * Strategy for selecting an AI model based on request parameters.
 */
public interface ModelSelectionStrategy {

    /**
     * Selects a model based on target and native languages.
     *
     * @param targetLanguage The target language (e.g., "Chinese", "English")
     * @param nativeLanguage The native language (e.g., "English", "Chinese")
     * @return The name of the selected model (e.g., "qwen", "openai", "zhipu")
     */
    String selectModel(String targetLanguage, String nativeLanguage);
}
