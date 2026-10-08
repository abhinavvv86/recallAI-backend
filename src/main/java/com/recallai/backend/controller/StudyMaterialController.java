package com.recallai.backend.controller;

import com.recallai.backend.model.StudyMaterial;
import com.recallai.backend.service.ConceptAIService;
import com.recallai.backend.service.QuizAIService;
import com.recallai.backend.service.StudyMaterialAIService;
import com.recallai.backend.service.StudyMaterialService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/materials")
public class StudyMaterialController {

    private final StudyMaterialService studyMaterialService;
    private final StudyMaterialAIService studyMaterialAIService;
    private final ConceptAIService conceptAIService;
    private final QuizAIService quizAIService;

    public StudyMaterialController(
            StudyMaterialService studyMaterialService,
            StudyMaterialAIService studyMaterialAIService,
            ConceptAIService conceptAIService,
            QuizAIService quizAIService
    ) {
        this.studyMaterialService = studyMaterialService;
        this.studyMaterialAIService = studyMaterialAIService;
        this.conceptAIService = conceptAIService;
        this.quizAIService = quizAIService;
    }

    @PostMapping
    public ResponseEntity<StudyMaterial> createMaterial(
            @RequestBody StudyMaterialRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        StudyMaterial material =
                studyMaterialService.createMaterial(
                        request.getTitle(),
                        request.getContent(),
                        request.getSubject(),
                        email
                );

        return ResponseEntity.ok(material);
    }

    @GetMapping
    public ResponseEntity<List<StudyMaterial>> getUserMaterials(
            Authentication authentication
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                studyMaterialService.getUserMaterials(email)
        );
    }

    @PostMapping("/{materialId}/summary")
    public ResponseEntity<Map<String, String>> generateSummary(
            @PathVariable Long materialId,
            Authentication authentication
    ) {

        String email = authentication.getName();

        String summary =
                studyMaterialAIService.generateSummary(
                        materialId,
                        email
                );

        return ResponseEntity.ok(
                Map.of(
                        "summary", summary
                )
        );
    }

    @PostMapping("/{materialId}/concepts")
    public ResponseEntity<Map<String, String>> extractConcepts(
            @PathVariable Long materialId,
            Authentication authentication
    ) {

        String email = authentication.getName();

        String concepts =
                conceptAIService.extractConcepts(
                        materialId,
                        email
                );

        return ResponseEntity.ok(
                Map.of(
                        "concepts", concepts
                )
        );
    }

    @PostMapping("/{materialId}/quiz")
    public ResponseEntity<Map<String, String>> generateQuiz(
            @PathVariable Long materialId,
            Authentication authentication
    ) {

        String email = authentication.getName();

        String quiz =
                quizAIService.generateQuiz(
                        materialId,
                        email
                );

        return ResponseEntity.ok(
                Map.of(
                        "quiz", quiz
                )
        );
    }

    public static class StudyMaterialRequest {

        private String title;
        private String content;
        private String subject;

        public StudyMaterialRequest() {
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }
    }
}