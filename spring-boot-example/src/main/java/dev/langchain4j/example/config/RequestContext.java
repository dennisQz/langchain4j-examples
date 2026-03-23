package dev.langchain4j.example.config;

public class RequestContext {
    private static final ThreadLocal<String> DEVICE_ID = new ThreadLocal<>();
    private static final ThreadLocal<Integer> SCENE_ID = new ThreadLocal<>();

    public static void setDeviceId(String deviceId) {
        DEVICE_ID.set(deviceId);
    }

    public static String getDeviceId() {
        return DEVICE_ID.get();
    }

    public static void setSceneId(Integer sceneId) {
        SCENE_ID.set(sceneId);
    }

    public static Integer getSceneId() {
        return SCENE_ID.get();
    }

    public static void clear() {
        DEVICE_ID.remove();
        SCENE_ID.remove();
    }
}
