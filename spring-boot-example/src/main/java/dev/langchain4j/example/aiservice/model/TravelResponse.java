package dev.langchain4j.example.aiservice.model;

import java.util.List;

public class TravelResponse {
    private List<TravelPhrase> phrases;
    private List<String> intentions;
    private String message;
    private List<String> fixedTextList;

    public TravelResponse() {
    }

    public TravelResponse(List<TravelPhrase> phrases) {
        this.phrases = phrases;
    }

    public TravelResponse(String message) {
        this.message = message;
    }

    public List<TravelPhrase> getPhrases() {
        return phrases;
    }

    public void setPhrases(List<TravelPhrase> phrases) {
        this.phrases = phrases;
    }

    public List<String> getIntentions() {
        return intentions;
    }

    public void setIntentions(List<String> intentions) {
        this.intentions = intentions;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getFixedTextList() {
        return fixedTextList;
    }

    public void setFixedTextList(List<String> fixedTextList) {
        this.fixedTextList = fixedTextList;
    }
}
