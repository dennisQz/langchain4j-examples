package dev.langchain4j.example.aiservice;

import dev.langchain4j.example.aiservice.common.ApiResponse;
import dev.langchain4j.example.aiservice.model.TravelRequest;
import dev.langchain4j.example.aiservice.model.TravelResponse;
import dev.langchain4j.example.configuration.ModelSelector;
import dev.langchain4j.example.service.ModelSelectionStrategy;
import dev.langchain4j.example.service.PromptManager;
import dev.langchain4j.model.input.PromptTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TravelController {

    private final TravelAssistant travelAssistant;
    private final ModelSelector modelSelector;
    private final ModelSelectionStrategy modelSelectionStrategy;
    private final PromptManager promptManager;

    public TravelController(TravelAssistant travelAssistant,
                            ModelSelector modelSelector,
                            ModelSelectionStrategy modelSelectionStrategy,
                            PromptManager promptManager) {
        this.travelAssistant = travelAssistant;
        this.modelSelector = modelSelector;
        this.modelSelectionStrategy = modelSelectionStrategy;
        this.promptManager = promptManager;
    }

    @PostMapping("/travel/assistant")
    public ApiResponse<TravelResponse> chat(@RequestBody TravelRequest request) {
        String sessionId = request.getSessionId() != null ? request.getSessionId() : "default";

        // Select model based on strategy
        String selectedModel = modelSelectionStrategy.selectModel(request.getTargetLanguage(), request.getNativeLanguage());
        modelSelector.setContextModel(selectedModel);

        try {
            // Load prompts
            String systemPrompt = promptManager.loadPrompt("travel", selectedModel, "system");
            String userPromptTemplate = promptManager.loadPrompt("travel", selectedModel, "user");

            // Fill user prompt template
            Map<String, Object> variables = new HashMap<>();
            variables.put("scene", request.getScene());
            variables.put("targetLanguage", request.getTargetLanguage());
            variables.put("nativeLanguage", request.getNativeLanguage());
            
            PromptTemplate template = PromptTemplate.from(userPromptTemplate);
            String userMessage = template.apply(variables).text();

            TravelResponse response = travelAssistant.chat(sessionId, systemPrompt, userMessage);

            if (response.getMessage() != null && !response.getMessage().isEmpty() && (response.getPhrases() == null || response.getPhrases().isEmpty())) {
                return ApiResponse.error(500, response);
            }

            return ApiResponse.success(response);
        } finally {
            modelSelector.clearContextModel();
        }
    }
}
