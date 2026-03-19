package dev.langchain4j.example.aiservice.model;

import java.util.List;

public class SceneTranslationResponse {
    private List<SceneTranslationPhrase> phrases;
    private List<String> intentions;
    private List<String> fixedTextList;

    public SceneTranslationResponse() {
    }

    public SceneTranslationResponse(List<SceneTranslationPhrase> phrases) {
        this.phrases = phrases;
    }

    public List<SceneTranslationPhrase> getPhrases() {
        return phrases;
    }

    public void setPhrases(List<SceneTranslationPhrase> phrases) {
        this.phrases = phrases;
    }

    public List<String> getIntentions() {
        return intentions;
    }

    public void setIntentions(List<String> intentions) {
        this.intentions = intentions;
    }

    public List<String> getFixedTextList() {
        return fixedTextList;
    }

    public void setFixedTextList(List<String> fixedTextList) {
        this.fixedTextList = fixedTextList;
    }
}
