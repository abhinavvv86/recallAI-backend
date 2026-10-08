package com.recallai.backend.controller;

import com.recallai.backend.service.RecommendationService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(
            RecommendationService recommendationService
    ) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/{subject}")
    public ResponseEntity<Map<String, String>> getRecommendation(
            @PathVariable String subject,
            Authentication authentication
    ) {

        String email = authentication.getName();

        String recommendation =
                recommendationService.generateRecommendation(
                        email,
                        subject
                );

        return ResponseEntity.ok(
                Map.of(
                        "recommendation",
                        recommendation
                )
        );
    }
}
