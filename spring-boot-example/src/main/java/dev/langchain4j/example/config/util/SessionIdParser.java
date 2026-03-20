package dev.langchain4j.example.config.util;

public class SessionIdParser {

    public record SessionInfo(String deviceId, String sceneId, String randomId) {}

    public static SessionInfo parse(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return new SessionInfo("default", "default", "");
        }
        
        String[] parts = sessionId.split("_");
        String deviceId = parts.length > 0 ? parts[0] : "default";
        String sceneId = parts.length > 1 ? parts[1] : "default";
        
        StringBuilder randomIdBuilder = new StringBuilder();
        for (int i = 2; i < parts.length; i++) {
            if (i > 2) {
                randomIdBuilder.append("_");
            }
            randomIdBuilder.append(parts[i]);
        }
        
        return new SessionInfo(deviceId, sceneId, randomIdBuilder.toString());
    }
}
