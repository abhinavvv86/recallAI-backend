package com.recallai.backend.controller;

import com.recallai.backend.service.AdaptiveLearningService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/adaptive-learning")
public class AdaptiveLearningController {

    private final AdaptiveLearningService adaptiveLearningService;

    public AdaptiveLearningController(
            AdaptiveLearningService adaptiveLearningService
    ) {
        this.adaptiveLearningService =
                adaptiveLearningService;
    }

    @GetMapping("/{subject}")
    public ResponseEntity<String> getNextStudyAction(
            @PathVariable String subject,
            Authentication authentication
    ) {

        String email = authentication.getName();

        String recommendation =
                adaptiveLearningService.generateNextStudyAction(
                        email,
                        subject
                );

        return ResponseEntity.ok(recommendation);
    }
}