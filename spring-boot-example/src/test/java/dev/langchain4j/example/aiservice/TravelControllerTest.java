package dev.langchain4j.example.aiservice;

import dev.langchain4j.example.aiservice.model.SceneTranslationPhrase;
import dev.langchain4j.example.aiservice.model.SceneTranslationResponse;
import dev.langchain4j.example.aiservice.model.TravelPhrase;
import dev.langchain4j.example.aiservice.model.TravelResponse;
import dev.langchain4j.example.config.ModelSelector;
import dev.langchain4j.example.config.ModelSelectionStrategy;
import dev.langchain4j.example.controller.TravelController;
import dev.langchain4j.example.service.LanguageMappingService;
import dev.langchain4j.example.service.PromptManager;
import dev.langchain4j.example.service.ScenePhraseService;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
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
    private TranslateScenesAssistant translateScenesAssistant;

    @MockBean
    private ModelSelector modelSelector;

    @MockBean
    private ModelSelectionStrategy modelSelectionStrategy;

    @MockBean
    private PromptManager promptManager;

    @MockBean
    private LanguageMappingService languageMappingService;

    @MockBean
    private ScenePhraseService scenePhraseService;

    @MockBean
    private ChatMemoryProvider ephemeralChatMemoryProvider;

    private ChatMemory chatMemory;

    @BeforeEach
    void setUp() {
        chatMemory = Mockito.mock(ChatMemory.class);
        given(modelSelectionStrategy.selectModel(anyString(), anyString())).willReturn("openai");
        
        given(promptManager.loadPrompt(eq("travel"), anyString(), eq("system"))).willReturn("System Prompt");
        given(promptManager.loadPrompt(eq("travel"), anyString(), eq("user"))).willReturn("User Prompt for {{scene}}");
        
        given(promptManager.loadPrompt(eq("translate-scenes"), anyString(), eq("system"))).willReturn("Translate System Prompt");
        given(promptManager.loadPrompt(eq("translate-scenes"), anyString(), eq("user"))).willReturn("Translate User Prompt for {{phrases}}");
        
        given(languageMappingService.getLanguageName(anyString())).willReturn("English");
        given(ephemeralChatMemoryProvider.get(anyString())).willReturn(chatMemory);
    }

    @Test
    void testChat() throws Exception {
        TravelPhrase phrase = new TravelPhrase("Hello", "Bonjour");
        TravelResponse response = new TravelResponse(List.of(phrase));

        given(travelAssistant.chat(eq("default"), eq("System Prompt"), anyString())).willReturn(response);

        mockMvc.perform(post("/travel/assistant")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"scene\": \"Paris\", \"targetLanguage\": \"French\", \"nativeLanguage\": \"English\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.phrases[0].original").value("Hello"))
                .andExpect(jsonPath("$.data.phrases[0].translated").value("Bonjour"))
                .andExpect(jsonPath("$.data.fixedTextList").isArray())
                .andExpect(jsonPath("$.data.fixedTextList[0]").value("您能为我提供我可以在Paris使用的10个常用短语吗?"));
    }

    @Test
    void testChatWithSessionId() throws Exception {
        TravelPhrase phrase = new TravelPhrase("Hello", "Bonjour");
        TravelResponse response = new TravelResponse(List.of(phrase));

        given(travelAssistant.chat(eq("traveler1"), eq("System Prompt"), anyString())).willReturn(response);

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
        TravelResponse response = new TravelResponse("Please provide more specific scene information.");

        given(travelAssistant.chat(eq("default"), eq("System Prompt"), anyString())).willReturn(response);

        mockMvc.perform(post("/travel/assistant")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"scene\": \"Unknown\", \"targetLanguage\": \"French\", \"nativeLanguage\": \"English\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.data.message").value("Please provide more specific scene information."));
    }

    @Test
    void testChatWithChinese() throws Exception {
        TravelPhrase phrase = new TravelPhrase("Hello", "你好");
        TravelResponse response = new TravelResponse(List.of(phrase));

        given(travelAssistant.chat(eq("default"), eq("System Prompt"), anyString())).willReturn(response);

        mockMvc.perform(post("/travel/assistant")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"scene\": \"Beijing\", \"targetLanguage\": \"Chinese\", \"nativeLanguage\": \"English\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.phrases[0].original").value("Hello"))
                .andExpect(jsonPath("$.data.phrases[0].translated").value("你好"));
    }

    @Test
    void testChatWithSceneId() throws Exception {
        List<String> chinesePhrases = Arrays.asList(
                "请问有预订吗",
                "请给我们一张桌子，两位",
                "请把菜单拿给我们看看"
        );

        given(scenePhraseService.getPhrasesBySceneId("1")).willReturn(chinesePhrases);

        SceneTranslationPhrase phrase = new SceneTranslationPhrase("Do you have a reservation?", "すみません、ご予約はありますか？");
        SceneTranslationResponse response = new SceneTranslationResponse(List.of(phrase));

        given(translateScenesAssistant.translate(eq("default_translate"), eq("Translate System Prompt"), anyString())).willReturn(response);

        mockMvc.perform(post("/travel/assistant")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"sceneId\": \"1\", \"targetLanguage\": \"Japanese\", \"nativeLanguage\": \"English\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.phrases[0].original").value("Do you have a reservation?"))
                .andExpect(jsonPath("$.data.phrases[0].translated").value("すみません、ご予約はありますか？"))
                .andExpect(jsonPath("$.data.fixedTextList").isArray());

        verify(ephemeralChatMemoryProvider).get("default_translate");
        verify(chatMemory).clear();
    }

    @Test
    void testChatWithInvalidSceneId() throws Exception {
        given(scenePhraseService.getPhrasesBySceneId("999")).willReturn(List.of());

        mockMvc.perform(post("/travel/assistant")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"sceneId\": \"999\", \"targetLanguage\": \"Japanese\", \"nativeLanguage\": \"English\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.data.message").value("未找到对应的场景，请检查sceneId是否正确"));
    }

    @Test
    void testChatWithSceneIdWithSessionId() throws Exception {
        List<String> chinesePhrases = Arrays.asList(
                "请问有预订吗",
                "请给我们一张桌子，两位"
        );

        given(scenePhraseService.getPhrasesBySceneId("1")).willReturn(chinesePhrases);

        SceneTranslationPhrase phrase = new SceneTranslationPhrase("Do you have a reservation?", "すみません、ご予約はありますか？");
        SceneTranslationResponse response = new SceneTranslationResponse(List.of(phrase));

        given(translateScenesAssistant.translate(eq("mysession_translate"), eq("Translate System Prompt"), anyString())).willReturn(response);

        mockMvc.perform(post("/travel/assistant")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"sceneId\": \"1\", \"targetLanguage\": \"Japanese\", \"nativeLanguage\": \"English\", \"sessionId\": \"mysession\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(ephemeralChatMemoryProvider).get("mysession_translate");
        verify(chatMemory).clear();
    }

    @Test
    void testChatWithFirstParameter() throws Exception {
        List<String> chinesePhrases = Arrays.asList(
                "请问有预订吗",
                "请给我们一张桌子，两位"
        );

        given(scenePhraseService.getPhrasesBySceneId("1")).willReturn(chinesePhrases);

        SceneTranslationPhrase phrase = new SceneTranslationPhrase("Do you have a reservation?", "すみません、ご予約はありますか？");
        SceneTranslationResponse response = new SceneTranslationResponse(List.of(phrase));

        given(translateScenesAssistant.translate(eq("device123_translate"), eq("Translate System Prompt"), anyString())).willReturn(response);

        mockMvc.perform(post("/travel/assistant")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"first\": 1, \"deviceId\": \"device123\", \"sceneId\": \"1\", \"targetLanguage\": \"Japanese\", \"nativeLanguage\": \"English\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(ephemeralChatMemoryProvider).get("device123_translate");
        verify(chatMemory).clear();
    }
}
