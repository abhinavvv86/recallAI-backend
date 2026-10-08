package com.recallai.backend.controller;

import com.recallai.backend.service.OllamaService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final OllamaService ollamaService;

    public AIController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    @PostMapping("/ask")
    public ResponseEntity<Map<String, String>> askAI(
            @RequestBody AIRequest request,
            Authentication authentication
    ) {

        String response = ollamaService.generateResponse(
                request.getPrompt()
        );

        return ResponseEntity.ok(
                Map.of(
                        "response", response
                )
        );
    }

    public static class AIRequest {

        private String prompt;

        public AIRequest() {
        }

        public String getPrompt() {
            return prompt;
        }

        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }
    }
}