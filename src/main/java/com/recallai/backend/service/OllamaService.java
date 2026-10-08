package com.recallai.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class OllamaService {

    private final RestClient ollamaClient;
    private final RestClient geminiClient;

    private final String ollamaModel;
    private final String geminiApiKey;
    private final String geminiModel;

    public OllamaService(
            @Value("${ollama.api.url}") String ollamaUrl,
            @Value("${ollama.model}") String ollamaModel,
            @Value("${gemini.api.key:}") String geminiApiKey,
            @Value("${gemini.model:gemini-2.5-flash}") String geminiModel
    ) {
        this.ollamaClient = RestClient.builder()
                .baseUrl(ollamaUrl)
                .build();

        this.geminiClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .build();

        this.ollamaModel = ollamaModel;
        this.geminiApiKey = geminiApiKey;
        this.geminiModel = geminiModel;
    }

    public String generateResponse(String prompt) {

        /*
         * If a Gemini API key is available, use Gemini.
         * This is used for the deployed version of RecallAI.
         */
        if (geminiApiKey != null && !geminiApiKey.isBlank()) {
            return generateWithGemini(prompt);
        }

        /*
         * If no Gemini key is available, use local Ollama.
         * This keeps the existing local development setup working.
         */
        return generateWithOllama(prompt);
    }

    private String generateWithOllama(String prompt) {

        Map<String, Object> request = Map.of(
                "model", ollamaModel,
                "prompt", prompt,
                "stream", false
        );

        Map<?, ?> response = ollamaClient.post()
                .uri("/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Map.class);

        if (response == null || response.get("response") == null) {
            throw new RuntimeException(
                    "No response received from Ollama"
            );
        }

        return response.get("response").toString();
    }

    private String generateWithGemini(String prompt) {

        Map<String, Object> request = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );

        Map<?, ?> response = geminiClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/models/" + geminiModel + ":generateContent")
                        .queryParam("key", geminiApiKey)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Map.class);

        if (response == null) {
            throw new RuntimeException(
                    "No response received from Gemini"
            );
        }

        Object candidatesObject = response.get("candidates");

        if (!(candidatesObject instanceof List<?> candidates)
                || candidates.isEmpty()) {
            throw new RuntimeException(
                    "Gemini returned no candidates"
            );
        }

        Object firstCandidate = candidates.get(0);

        if (!(firstCandidate instanceof Map<?, ?> candidate)) {
            throw new RuntimeException(
                    "Invalid Gemini response"
            );
        }

        Object contentObject = candidate.get("content");

        if (!(contentObject instanceof Map<?, ?> content)) {
            throw new RuntimeException(
                    "Gemini response does not contain content"
            );
        }

        Object partsObject = content.get("parts");

        if (!(partsObject instanceof List<?> parts)
                || parts.isEmpty()) {
            throw new RuntimeException(
                    "Gemini response does not contain text"
            );
        }

        Object firstPart = parts.get(0);

        if (!(firstPart instanceof Map<?, ?> part)
                || part.get("text") == null) {
            throw new RuntimeException(
                    "Gemini response does not contain text"
            );
        }

        return part.get("text").toString();
    }
}