package dev.langchain4j.example.aiservice.model;

import java.util.List;

public class TravelResponse {
    private List<TravelPhrase> phrases;
    private String message;
    private String startWords;

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
