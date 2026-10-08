package com.recallai.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class OllamaService {

    private final RestClient restClient;
    private final String model;

    public OllamaService(
            @Value("${ollama.api.url}") String ollamaUrl,
            @Value("${ollama.model}") String model
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(ollamaUrl)
                .build();

        this.model = model;
    }

    public String generateResponse(String prompt) {

        Map<String, Object> request = Map.of(
                "model", model,
                "prompt", prompt,
                "stream", false
        );

        Map<?, ?> response = restClient.post()
                .uri("/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Map.class);

        if (response == null || response.get("response") == null) {
            throw new RuntimeException("No response received from Ollama");
        }

        return response.get("response").toString();
    }
}