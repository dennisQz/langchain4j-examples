package dev.langchain4j.example.controller;

import dev.langchain4j.example.config.ModelSelector;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/model")
public class ModelSwitchController {

    private final ModelSelector modelSelector;

    public ModelSwitchController(ModelSelector modelSelector) {
        this.modelSelector = modelSelector;
    }

    @GetMapping("/current")
    public String getCurrentModel() {
        return modelSelector.getCurrentModel();
    }

    @PostMapping("/switch")
    public String switchModel(@RequestParam("name") String name) {
        if (!name.equals("openai") && !name.equals("qwen") && !name.equals("zhipu")) {
            throw new IllegalArgumentException("Unknown model: " + name + ". Available models: openai, qwen, zhipu");
        }
        modelSelector.setCurrentModel(name);
        return "Switched to " + name;
    }
}
