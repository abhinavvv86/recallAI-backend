package com.recallai.backend.controller;

import com.recallai.backend.model.Concept;
import com.recallai.backend.service.ConceptService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/concepts")
public class ConceptController {

    private final ConceptService conceptService;

    public ConceptController(ConceptService conceptService) {
        this.conceptService = conceptService;
    }

    @PostMapping
    public ResponseEntity<Concept> createConcept(
            @RequestBody ConceptRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        Concept concept = conceptService.createConcept(
                request.getName(),
                request.getDescription(),
                request.getSubject(),
                email
        );

        return ResponseEntity.ok(concept);
    }

    @GetMapping
    public ResponseEntity<List<Concept>> getUserConcepts(
            Authentication authentication
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                conceptService.getUserConcepts(email)
        );
    }

    @GetMapping("/subject/{subject}")
    public ResponseEntity<List<Concept>> getConceptsBySubject(
            @PathVariable String subject,
            Authentication authentication
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                conceptService.getUserConceptsBySubject(
                        email,
                        subject
                )
        );
    }

    public static class ConceptRequest {

        private String name;
        private String description;
        private String subject;

        public ConceptRequest() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }
    }
}