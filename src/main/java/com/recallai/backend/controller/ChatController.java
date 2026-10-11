
package com.recallai.backend.controller;

import com.recallai.backend.service.OllamaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final OllamaService ollamaService;

    public ChatController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> chat(
            @RequestBody Map<String, String> request) {

        String message = request.get("message");

        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Message cannot be empty."));
        }

        try {
            String prompt = """
                    You are RecallAI, an AI study assistant.
                    Help students understand concepts clearly.
                    Use simple language, step-by-step explanations,
                    and examples when useful.
                    Stay focused on learning and studying.

                    Student's question:
                    """ + message;

            String response = ollamaService.generateResponse(prompt);

            return ResponseEntity.ok(Map.of("response", response));

        } catch (Exception e) {
    e.printStackTrace();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of(
                    "error",
                    "The AI assistant could not generate a response. Please try again."
            ));
}
    }
}
