
package com.recallai.backend.controller;

import com.recallai.backend.service.OllamaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
            @RequestBody Map<String, Object> request) {

        String message = request.get("message") instanceof String
                ? (String) request.get("message")
                : "";

        if (message.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Message cannot be empty."));
        }

        try {
            StringBuilder prompt = new StringBuilder("""
                    You are RecallAI, an intelligent AI study assistant.
                    Help students understand concepts clearly using simple
                    language, step-by-step explanations, and useful examples.

                    IMPORTANT CONVERSATION RULES:
                    - Remember and use the conversation history provided below.
                    - Understand follow-up questions using the previous messages.
                    - If the student says "give me 3 more examples", use the
                      topic discussed earlier. Do not ask them to repeat it.
                    - If the student asks for more examples, provide new examples.
                    - Ask for clarification only when the topic is genuinely unclear.
                    - Stay focused on learning and studying.

                    CONVERSATION HISTORY:
                    """);

            Object historyObject = request.get("history");

            if (historyObject instanceof List<?> history) {
                int start = Math.max(0, history.size() - 12);

                for (int i = start; i < history.size(); i++) {
                    Object item = history.get(i);

                    if (item instanceof Map<?, ?> entry) {
                        Object roleObject = entry.get("role");
                        Object contentObject = entry.get("content");

                        if (roleObject instanceof String role
                                && contentObject instanceof String content
                                && !content.isBlank()
                                && (role.equals("user")
                                || role.equals("assistant"))) {

                            prompt.append(role.equals("user")
                                    ? "\nStudent: "
                                    : "\nRecallAI: ");
                            prompt.append(content).append("\n");
                        }
                    }
                }
            }

            prompt.append("\nStudent's latest question:\n")
                    .append(message)
                    .append("\n\nRespond naturally, using the previous "
                            + "conversation whenever relevant.");

            String response = ollamaService.generateResponse(prompt.toString());

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
