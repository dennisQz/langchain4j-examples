package dev.langchain4j.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ScenePhraseService {

    private static final Logger log = LoggerFactory.getLogger(ScenePhraseService.class);
    private static final String SCENE_PHRASES_PATH = "classpath:scene-phrases.yml";

    private final ResourceLoader resourceLoader;
    private List<SceneInfo> scenes;

    public ScenePhraseService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void init() {
        loadScenePhrases();
    }

    @SuppressWarnings("unchecked")
    private void loadScenePhrases() {
        try {
            Resource resource = resourceLoader.getResource(SCENE_PHRASES_PATH);
            InputStream inputStream = resource.getInputStream();
            Yaml yaml = new Yaml();
            Map<String, Object> root = yaml.load(inputStream);
            
            List<Map<String, Object>> sceneList = (List<Map<String, Object>>) root.get("scenes");
            scenes = new ArrayList<>();
            
            for (Map<String, Object> sceneMap : sceneList) {
                SceneInfo scene = new SceneInfo();
                scene.setSceneId((Integer) sceneMap.get("sceneId"));
                scene.setScene((String) sceneMap.get("scene"));
                scene.setIntentions((List<String>) sceneMap.get("intentions"));
                scene.setPhrases((List<String>) sceneMap.get("phrases"));
                scenes.add(scene);
            }
            
            log.info("Loaded {} scenes from scene-phrases.yml", scenes.size());
        } catch (Exception e) {
            log.error("Failed to load scene-phrases.yml", e);
            scenes = new ArrayList<>();
        }
    }

    public List<String> getPhrasesBySceneId(Integer sceneId) {
        if (sceneId == null) {
            return new ArrayList<>();
        }
        
        for (SceneInfo scene : scenes) {
            if (scene.getSceneId().equals(sceneId)) {
                return scene.getPhrases();
            }
        }
        
        log.warn("Scene not found for sceneId: {}", sceneId);
        return new ArrayList<>();
    }

    public List<String> getPhrasesBySceneId(String sceneIdStr) {
        try {
            Integer sceneId = Integer.parseInt(sceneIdStr);
            return getPhrasesBySceneId(sceneId);
        } catch (NumberFormatException e) {
            log.warn("Invalid sceneId format: {}", sceneIdStr);
            return new ArrayList<>();
        }
    }

    public SceneInfo getSceneBySceneId(Integer sceneId) {
        if (sceneId == null) {
            return null;
        }
        
        for (SceneInfo scene : scenes) {
            if (scene.getSceneId().equals(sceneId)) {
                return scene;
            }
        }
        return null;
    }

    public static class SceneInfo {
        private Integer sceneId;
        private String scene;
        private List<String> intentions;
        private List<String> phrases;

        public Integer getSceneId() {
            return sceneId;
        }

        public void setSceneId(Integer sceneId) {
            this.sceneId = sceneId;
        }

        public String getScene() {
            return scene;
        }

        public void setScene(String scene) {
            this.scene = scene;
        }

        public List<String> getIntentions() {
            return intentions;
        }

        public void setIntentions(List<String> intentions) {
            this.intentions = intentions;
        }

        public List<String> getPhrases() {
            return phrases;
        }

        public void setPhrases(List<String> phrases) {
            this.phrases = phrases;
        }
    }
}
