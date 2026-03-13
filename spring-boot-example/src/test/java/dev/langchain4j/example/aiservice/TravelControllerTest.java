package dev.langchain4j.example.aiservice;

import dev.langchain4j.example.aiservice.model.TravelPhrase;
import dev.langchain4j.example.aiservice.model.TravelResponse;
import dev.langchain4j.example.configuration.ModelSelector;
import dev.langchain4j.example.service.ModelSelectionStrategy;
import dev.langchain4j.example.service.PromptManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TravelController.class)
class TravelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TravelAssistant travelAssistant;

    @MockBean
    private ModelSelector modelSelector;

    @MockBean
    private ModelSelectionStrategy modelSelectionStrategy;

    @MockBean
    private PromptManager promptManager;

    @BeforeEach
    void setUp() {
        given(modelSelectionStrategy.selectModel(anyString(), anyString())).willReturn("openai");
        given(promptManager.loadPrompt(anyString(), anyString(), eq("system"))).willReturn("System Prompt");
        given(promptManager.loadPrompt(anyString(), anyString(), eq("user"))).willReturn("User Prompt for {{scene}}");
    }

    @Test
    void testChat() throws Exception {
        // Mock data
        TravelPhrase phrase = new TravelPhrase("Hello", "Bonjour");
        TravelResponse response = new TravelResponse(List.of(phrase));

        given(travelAssistant.chat(eq("default"), eq("System Prompt"), anyString())).willReturn(response);

        // Perform request
        mockMvc.perform(post("/travel/assistant")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"scene\": \"Paris\", \"targetLanguage\": \"French\", \"nativeLanguage\": \"English\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.phrases[0].original").value("Hello"))
                .andExpect(jsonPath("$.data.phrases[0].translated").value("Bonjour"));
    }

    @Test
    void testChatWithSessionId() throws Exception {
        // Mock data
        TravelPhrase phrase = new TravelPhrase("Hello", "Bonjour");
        TravelResponse response = new TravelResponse(List.of(phrase));

        given(travelAssistant.chat(eq("traveler1"), eq("System Prompt"), anyString())).willReturn(response);

        // Perform request
        mockMvc.perform(post("/travel/assistant")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"scene\": \"Paris\", \"targetLanguage\": \"French\", \"nativeLanguage\": \"English\", \"sessionId\": \"traveler1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.phrases[0].original").value("Hello"))
                .andExpect(jsonPath("$.data.phrases[0].translated").value("Bonjour"));
    }

    @Test
    void testChatWithAmbiguousScene() throws Exception {
        // Mock data
        TravelResponse response = new TravelResponse("Please provide more specific scene information.");

        given(travelAssistant.chat(eq("default"), eq("System Prompt"), anyString())).willReturn(response);

        // Perform request
        mockMvc.perform(post("/travel/assistant")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"scene\": \"Unknown\", \"targetLanguage\": \"French\", \"nativeLanguage\": \"English\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.data.message").value("Please provide more specific scene information."));
    }

    @Test
    void testChatWithPhonetic() throws Exception {
        // Mock data
        TravelPhrase phrase = new TravelPhrase("Hello", "你好", "Ni Hao");
        TravelResponse response = new TravelResponse(List.of(phrase));

        given(travelAssistant.chat(eq("default"), eq("System Prompt"), anyString())).willReturn(response);

        // Perform request
        mockMvc.perform(post("/travel/assistant")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"scene\": \"Beijing\", \"targetLanguage\": \"Chinese\", \"nativeLanguage\": \"English\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.phrases[0].original").value("Hello"))
                .andExpect(jsonPath("$.data.phrases[0].translated").value("你好"))
                .andExpect(jsonPath("$.data.phrases[0].phonetic").value("Ni Hao"));
    }
}
