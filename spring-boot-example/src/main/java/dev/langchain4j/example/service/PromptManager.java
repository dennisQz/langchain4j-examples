package dev.langchain4j.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@Service
public class PromptManager {

    private static final Logger log = LoggerFactory.getLogger(PromptManager.class);
    private final ResourceLoader resourceLoader;

    public PromptManager(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String loadPrompt(String agent, String model, String type) {
        // 1. Try specific model prompt
        String path = String.format("classpath:prompts/%s/%s/%s.st", agent, model, type);
        try {
            return loadResource(path);
        } catch (IOException e) {
            log.debug("Prompt not found at {}, trying default", path);
        }

        // 2. Try default prompt
        String defaultPath = String.format("classpath:prompts/%s/default/%s.st", agent, type);
        try {
            return loadResource(defaultPath);
        } catch (IOException e) {
            log.error("Default prompt not found at {}", defaultPath);
            throw new RuntimeException("Prompt not found: " + defaultPath, e);
        }
    }

    private String loadResource(String path) throws IOException {
        Resource resource = resourceLoader.getResource(path);
        if (!resource.exists()) {
            throw new IOException("Resource not found: " + path);
        }
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        }
    }
}
