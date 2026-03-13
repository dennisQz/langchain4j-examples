package dev.langchain4j.example.aiservice;

import dev.langchain4j.example.aiservice.common.ApiResponse;
import dev.langchain4j.example.aiservice.model.TravelRequest;
import dev.langchain4j.example.aiservice.model.TravelResponse;
import dev.langchain4j.example.configuration.ModelSelector;
import dev.langchain4j.example.service.ModelSelectionStrategy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TravelController {

    private final TravelAssistant travelAssistant;
    private final ModelSelector modelSelector;
    private final ModelSelectionStrategy modelSelectionStrategy;

    public TravelController(TravelAssistant travelAssistant,
                            ModelSelector modelSelector,
                            ModelSelectionStrategy modelSelectionStrategy) {
        this.travelAssistant = travelAssistant;
        this.modelSelector = modelSelector;
        this.modelSelectionStrategy = modelSelectionStrategy;
    }

    @PostMapping("/travel/assistant")
    public ApiResponse<TravelResponse> chat(@RequestBody TravelRequest request) {
        String sessionId = request.getSessionId() != null ? request.getSessionId() : "default";

        // Select model based on strategy
        String selectedModel = modelSelectionStrategy.selectModel(request.getTargetLanguage(), request.getNativeLanguage());
        modelSelector.setContextModel(selectedModel);

        try {
            TravelResponse response = travelAssistant.chat(sessionId, request.getScene(), request.getTargetLanguage(), request.getNativeLanguage());

            if (response.getMessage() != null && !response.getMessage().isEmpty() && (response.getPhrases() == null || response.getPhrases().isEmpty())) {
                return ApiResponse.error(500, response);
            }

            return ApiResponse.success(response);
        } finally {
            modelSelector.clearContextModel();
        }
    }
}
