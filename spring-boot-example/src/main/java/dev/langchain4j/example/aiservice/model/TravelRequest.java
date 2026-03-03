package dev.langchain4j.example.aiservice.model;

public class TravelRequest {
    private String scene;
    private String targetLanguage;
    private String nativeLanguage;
    private String sessionId;

    public TravelRequest() {
    }

    public TravelRequest(String scene, String targetLanguage, String nativeLanguage, String sessionId) {
        this.scene = scene;
        this.targetLanguage = targetLanguage;
        this.nativeLanguage = nativeLanguage;
        this.sessionId = sessionId;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    public String getNativeLanguage() {
        return nativeLanguage;
    }

    public void setNativeLanguage(String nativeLanguage) {
        this.nativeLanguage = nativeLanguage;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
