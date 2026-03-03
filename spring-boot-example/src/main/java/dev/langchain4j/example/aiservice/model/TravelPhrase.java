package dev.langchain4j.example.aiservice.model;

public class TravelPhrase {
    private String original;
    private String translated;
    private String phonetic;

    public TravelPhrase() {
    }

    public TravelPhrase(String original, String translated) {
        this.original = original;
        this.translated = translated;
    }

    public TravelPhrase(String original, String translated, String phonetic) {
        this.original = original;
        this.translated = translated;
        this.phonetic = phonetic;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getTranslated() {
        return translated;
    }

    public void setTranslated(String translated) {
        this.translated = translated;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }
}
