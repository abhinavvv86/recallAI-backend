package com.recallai.backend.controller;

import com.recallai.backend.model.Concept;
import com.recallai.backend.service.MasteryService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mastery")
public class MasteryController {

    private final MasteryService masteryService;

    public MasteryController(MasteryService masteryService) {
        this.masteryService = masteryService;
    }

    @PostMapping("/update")
    public ResponseEntity<List<Concept>> updateMastery(
            @RequestBody MasteryRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                masteryService.updateMastery(
                        email,
                        request.getSubject(),
                        request.getQuizScore()
                )
        );
    }

    @GetMapping("/weak/{subject}")
    public ResponseEntity<List<Concept>> getWeakConcepts(
            @PathVariable String subject,
            Authentication authentication
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                masteryService.getWeakConcepts(
                        email,
                        subject
                )
        );
    }

    public static class MasteryRequest {

        private String subject;
        private double quizScore;

        public MasteryRequest() {
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public double getQuizScore() {
            return quizScore;
        }

        public void setQuizScore(double quizScore) {
            this.quizScore = quizScore;
        }
    }
}