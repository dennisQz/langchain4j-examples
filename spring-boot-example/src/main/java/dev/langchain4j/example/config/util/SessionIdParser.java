package dev.langchain4j.example.config.util;

public class SessionIdParser {

    public record SessionInfo(String deviceId, Integer sceneId, String randomId) {}

    public static SessionInfo parse(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return new SessionInfo("default", 0, "");
        }
        
        String[] parts = sessionId.split("_");
        String deviceId = parts.length > 0 ? parts[0] : "default";
        String sceneIdStr = parts.length > 1 ? parts[1] : "0";
        
        Integer sceneId = 0;
        if (isNumeric(sceneIdStr)) {
            try {
                sceneId = Integer.parseInt(sceneIdStr);
            } catch (NumberFormatException e) {
                // Should not happen because of isNumeric check
            }
        }
        
        StringBuilder randomIdBuilder = new StringBuilder();
        for (int i = 2; i < parts.length; i++) {
            if (i > 2) {
                randomIdBuilder.append("_");
            }
            randomIdBuilder.append(parts[i]);
        }
        
        return new SessionInfo(deviceId, sceneId, randomIdBuilder.toString());
    }

    private static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
}
