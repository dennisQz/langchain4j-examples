package dev.langchain4j.example.aiservice;

import dev.langchain4j.example.aiservice.common.ApiResponse;
import dev.langchain4j.example.aiservice.model.TravelRequest;
import dev.langchain4j.example.aiservice.model.TravelResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TravelController {

    private final TravelAssistant travelAssistant;

    public TravelController(TravelAssistant travelAssistant) {
        this.travelAssistant = travelAssistant;
    }

    @PostMapping("/travel/assistant")
    public ApiResponse<TravelResponse> chat(@RequestBody TravelRequest request) {
        String sessionId = request.getSessionId() != null ? request.getSessionId() : "default";
        TravelResponse response = travelAssistant.chat(sessionId, request.getScene(), request.getTargetLanguage(), request.getNativeLanguage());

        if (response.getMessage() != null && !response.getMessage().isEmpty() && (response.getPhrases() == null || response.getPhrases().isEmpty())) {
            return ApiResponse.error(500, response);
        }

        return ApiResponse.success(response);
    }
}
