package dev.langchain4j.example.aiservice.model;

import java.util.List;

public class SceneTranslationResponse {
    private List<SceneTranslationPhrase> phrases;
    private String message;
    private String startWords;

    public SceneTranslationResponse() {
    }

    public SceneTranslationResponse(List<SceneTranslationPhrase> phrases) {
        this.phrases = phrases;
    }

    public SceneTranslationResponse(String message) {
        this.message = message;
    }

    public List<SceneTranslationPhrase> getPhrases() {
        return phrases;
    }

    public void setPhrases(List<SceneTranslationPhrase> phrases) {
        this.phrases = phrases;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStartWords() {
        return startWords;
    }

    public void setStartWords(String startWords) {
        this.startWords = startWords;
    }
}
