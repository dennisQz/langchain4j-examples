package dev.langchain4j.example.aiservice;

import dev.langchain4j.example.aiservice.common.ApiResponse;
import dev.langchain4j.example.aiservice.model.SceneTranslationResponse;
import dev.langchain4j.example.aiservice.model.TravelPhrase;
import dev.langchain4j.example.aiservice.model.TravelRequest;
import dev.langchain4j.example.aiservice.model.TravelResponse;
import dev.langchain4j.example.configuration.ModelSelector;
import dev.langchain4j.example.service.LanguageMappingService;
import dev.langchain4j.example.service.ModelSelectionStrategy;
import dev.langchain4j.example.service.PromptManager;
import dev.langchain4j.example.service.ScenePhraseService;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.model.input.PromptTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class TravelController {

    private final TravelAssistant travelAssistant;
    private final TranslateScenesAssistant translateScenesAssistant;
    private final ModelSelector modelSelector;
    private final ModelSelectionStrategy modelSelectionStrategy;
    private final PromptManager promptManager;
    private final LanguageMappingService languageMappingService;
    private final ScenePhraseService scenePhraseService;
    private final ChatMemoryProvider ephemeralChatMemoryProvider;

    private static final Logger log = LoggerFactory.getLogger(TravelController.class);


    public TravelController(TravelAssistant travelAssistant,
                            TranslateScenesAssistant translateScenesAssistant,
                            ModelSelector modelSelector,
                            ModelSelectionStrategy modelSelectionStrategy,
                            PromptManager promptManager,
                            LanguageMappingService languageMappingService,
                            ScenePhraseService scenePhraseService,
                            ChatMemoryProvider ephemeralChatMemoryProvider) {
        this.travelAssistant = travelAssistant;
        this.translateScenesAssistant = translateScenesAssistant;
        this.modelSelector = modelSelector;
        this.modelSelectionStrategy = modelSelectionStrategy;
        this.promptManager = promptManager;
        this.languageMappingService = languageMappingService;
        this.scenePhraseService = scenePhraseService;
        this.ephemeralChatMemoryProvider = ephemeralChatMemoryProvider;
    }

    @PostMapping("/travel/assistant")
    public ApiResponse<TravelResponse> chat(@RequestBody TravelRequest request) {
        String sessionId = request.getSessionId() != null ? request.getSessionId() : "default";

        String selectedModel = modelSelectionStrategy.selectModel(request.getTargetLanguage(), request.getNativeLanguage());
        modelSelector.setContextModel(selectedModel);

        try {
            if (request.getSceneId() != null && !request.getSceneId().isEmpty()) {
                return handleSceneIdRequest(request, selectedModel, sessionId);
            } else {
                return handleGenerateRequest(request, selectedModel, sessionId);
            }
        } finally {
            modelSelector.clearContextModel();
        }
    }

    private ApiResponse<TravelResponse> handleSceneIdRequest(TravelRequest request, String selectedModel, String sessionId) {
        log.info("处理sceneId请求: sceneId={}", request.getSceneId());

        List<String> chinesePhrases = scenePhraseService.getPhrasesBySceneId(request.getSceneId());
        if (chinesePhrases.isEmpty()) {
            log.warn("未找到对应的场景短语: sceneId={}", request.getSceneId());
            TravelResponse response = new TravelResponse("未找到对应的场景，请检查sceneId是否正确");
            return ApiResponse.error(500, response);
        }

        String targetLanguageName = languageMappingService.getLanguageName(request.getTargetLanguage());
        String nativeLanguageName = languageMappingService.getLanguageName(request.getNativeLanguage());
        log.info("语言映射: targetLanguage [{}] -> [{}], nativeLanguage [{}] -> [{}]",
                request.getTargetLanguage(), targetLanguageName, request.getNativeLanguage(), nativeLanguageName);

        String systemPrompt = promptManager.loadPrompt("translate-scenes", selectedModel, "system");
        String userPromptTemplate = promptManager.loadPrompt("translate-scenes", selectedModel, "user");

        Map<String, Object> variables = new HashMap<>();
        variables.put("phrases", String.join("\n", chinesePhrases));
        variables.put("targetLanguage", targetLanguageName);
        variables.put("nativeLanguage", nativeLanguageName);
        variables.put("scene", request.getScene());

        PromptTemplate sysTemplate = PromptTemplate.from(systemPrompt);
        String systemMessage = sysTemplate.apply(variables).text();

        PromptTemplate template = PromptTemplate.from(userPromptTemplate);
        String userMessage = template.apply(variables).text();

        String translationSessionId = sessionId + "_translate";
        SceneTranslationResponse sceneResponse = translateScenesAssistant.translate(translationSessionId, systemMessage, userMessage);

        ephemeralChatMemoryProvider.get(translationSessionId).clear();

        TravelResponse response = new TravelResponse();
        response.setMessage(sceneResponse.getMessage());
        response.setStartWords(sceneResponse.getStartWords());
        if (sceneResponse.getPhrases() != null) {
            response.setPhrases(sceneResponse.getPhrases().stream()
                    .map(p -> new TravelPhrase(p.getOriginal(), p.getTranslated()))
                    .toList());
        }

        if (response.getMessage() != null && !response.getMessage().isEmpty() && (response.getPhrases() == null || response.getPhrases().isEmpty())) {
            return ApiResponse.error(500, response);
        }

        log.info("sceneId请求结束###############################################");
        return ApiResponse.success(response);
    }

    private ApiResponse<TravelResponse> handleGenerateRequest(TravelRequest request, String selectedModel, String sessionId) {
        log.info("处理生成请求: scene={}", request.getScene());

        String systemPrompt = promptManager.loadPrompt("travel", selectedModel, "system");
        String userPromptTemplate = promptManager.loadPrompt("travel", selectedModel, "user");

        String targetLanguageName = languageMappingService.getLanguageName(request.getTargetLanguage());
        String nativeLanguageName = languageMappingService.getLanguageName(request.getNativeLanguage());
        log.info("语言映射: targetLanguage [{}] -> [{}], nativeLanguage [{}] -> [{}]",
                request.getTargetLanguage(), targetLanguageName, request.getNativeLanguage(), nativeLanguageName);

        Map<String, Object> variables = new HashMap<>();
        variables.put("scene", request.getScene());
        variables.put("targetLanguage", targetLanguageName);
        variables.put("nativeLanguage", nativeLanguageName);

        PromptTemplate sysTemplate = PromptTemplate.from(systemPrompt);
        String systemMessage = sysTemplate.apply(variables).text();

        PromptTemplate template = PromptTemplate.from(userPromptTemplate);
        String userMessage = template.apply(variables).text();

        TravelResponse response = travelAssistant.chat(sessionId, systemMessage, userMessage);

        if (response.getMessage() != null && !response.getMessage().isEmpty() && (response.getPhrases() == null || response.getPhrases().isEmpty())) {
            return ApiResponse.error(500, response);
        }
        log.info("生成请求结束###############################################");

        return ApiResponse.success(response);
    }
}
