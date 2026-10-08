package com.recallai.backend.controller;

import com.recallai.backend.model.QuizResult;
import com.recallai.backend.service.MasteryService;
import com.recallai.backend.service.QuizResultService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz-results")
public class QuizResultController {

    private final QuizResultService quizResultService;
    private final MasteryService masteryService;

    public QuizResultController(
            QuizResultService quizResultService,
            MasteryService masteryService
    ) {
        this.quizResultService = quizResultService;
        this.masteryService = masteryService;
    }

    @PostMapping
    public ResponseEntity<QuizResult> saveResult(
            @RequestBody QuizResultRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        QuizResult result =
                quizResultService.saveResult(
                        request.getTotalQuestions(),
                        request.getCorrectAnswers(),
                        request.getSubject(),
                        email
                );

        double quizScore = result.getScore();

        masteryService.updateMastery(
                email,
                request.getSubject(),
                quizScore
        );

        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<QuizResult>> getResults(
            Authentication authentication
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                quizResultService.getUserResults(email)
        );
    }

    @GetMapping("/subject/{subject}")
    public ResponseEntity<List<QuizResult>> getResultsBySubject(
            @PathVariable String subject,
            Authentication authentication
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                quizResultService.getUserResultsBySubject(
                        email,
                        subject
                )
        );
    }

    public static class QuizResultRequest {

        private int totalQuestions;
        private int correctAnswers;
        private String subject;

        public QuizResultRequest() {
        }

        public int getTotalQuestions() {
            return totalQuestions;
        }

        public void setTotalQuestions(int totalQuestions) {
            this.totalQuestions = totalQuestions;
        }

        public int getCorrectAnswers() {
            return correctAnswers;
        }

        public void setCorrectAnswers(int correctAnswers) {
            this.correctAnswers = correctAnswers;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }
    }
}