package dev.langchain4j.example.aiservice.model;

public class TravelRequest {
    private String scene;
    private String sceneId;
    private String targetLanguage;
    private String nativeLanguage;
    private String sessionId;
    private Integer first;
    private String deviceId;

    public TravelRequest() {
    }

    public TravelRequest(String scene, String sceneId, String targetLanguage, String nativeLanguage, String sessionId, Integer first, String deviceId) {
        this.scene = scene;
        this.sceneId = sceneId;
        this.targetLanguage = targetLanguage;
        this.nativeLanguage = nativeLanguage;
        this.sessionId = sessionId;
        this.first = first;
        this.deviceId = deviceId;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
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

    public Integer getFirst() {
        return first;
    }

    public void setFirst(Integer first) {
        this.first = first;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
