package dev.langchain4j.example.service;

import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class LanguageModelSelectionStrategy implements ModelSelectionStrategy {

    private static final String MODEL_QWEN = "qwen";
    private static final String MODEL_OPENAI = "openai";
    private static final String MODEL_ZHIPU = "zhipu";

    private static final Set<String> CHINESE_CODES = Set.of(
            "zh", "zh-cn", "zh-hans", "zh-hant", "zh-tw", "zh-hk"
    );

    private static final Set<String> ENGLISH_CODES = Set.of(
            "en", "en-us", "en-gb", "en-au", "en-ca", "en-nz", "en-za", "en-ph"
    );

    @Override
    public String selectModel(String targetLanguage, String nativeLanguage) {
        return MODEL_QWEN;
        // if (isChinese(targetLanguage) || isChinese(nativeLanguage)) {
        //     return MODEL_QWEN;
        // } else if (isEnglish(targetLanguage) || isEnglish(nativeLanguage)) {
        //     return MODEL_ZHIPU;
        // } 
        // return MODEL_OPENAI;
    }

    private boolean isChinese(String language) {
        if (language == null) {
            return false;
        }
        String lang = language.toLowerCase().trim();
        if (CHINESE_CODES.contains(lang)) {
            return true;
        }
        return lang.contains("chinese") || lang.contains("中文") || lang.contains("mandarin");
    }

    private boolean isEnglish(String language) {
        if (language == null) {
            return false;
        }
        String lang = language.toLowerCase().trim();
        if (ENGLISH_CODES.contains(lang)) {
            return true;
        }
        return lang.contains("english") || lang.contains("英文") || lang.contains("英语");
    }
}
