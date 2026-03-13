package dev.langchain4j.example.service;

import org.springframework.stereotype.Service;

@Service
public class LanguageModelSelectionStrategy implements ModelSelectionStrategy {

    private static final String MODEL_QWEN = "qwen";
    private static final String MODEL_OPENAI = "openai";
    private static final String MODEL_ZHIPU = "zhipu"; // Available but using Qwen for Chinese by default

    @Override
    public String selectModel(String targetLanguage, String nativeLanguage) {
        if (isChinese(targetLanguage) || isChinese(nativeLanguage)) {
            return MODEL_QWEN;
        } else if (isEnglish(targetLanguage) || isEnglish(nativeLanguage)) {
            return MODEL_ZHIPU;
        }
        return MODEL_OPENAI;
    }

    private boolean isChinese(String language) {
        if (language == null) {
            return false;
        }
        String lang = language.toLowerCase();
        return lang.contains("chinese") || lang.contains("中文") || lang.contains("mandarin");
    }

    private boolean isEnglish(String language) {
        if (language == null) {
            return false;
        }
        String lang = language.toLowerCase();
        return lang.contains("english") || lang.contains("英文") || lang.contains("英语");
    }
}
