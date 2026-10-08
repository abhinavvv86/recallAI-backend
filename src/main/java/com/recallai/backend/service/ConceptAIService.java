package com.recallai.backend.service;

import com.recallai.backend.model.Concept;
import com.recallai.backend.model.StudyMaterial;
import com.recallai.backend.model.User;
import com.recallai.backend.repository.ConceptRepository;
import com.recallai.backend.repository.StudyMaterialRepository;
import com.recallai.backend.repository.UserRepository;

import org.springframework.stereotype.Service;

@Service
public class ConceptAIService {

    private final StudyMaterialRepository studyMaterialRepository;
    private final ConceptRepository conceptRepository;
    private final UserRepository userRepository;
    private final OllamaService ollamaService;

    public ConceptAIService(
            StudyMaterialRepository studyMaterialRepository,
            ConceptRepository conceptRepository,
            UserRepository userRepository,
            OllamaService ollamaService
    ) {
        this.studyMaterialRepository = studyMaterialRepository;
        this.conceptRepository = conceptRepository;
        this.userRepository = userRepository;
        this.ollamaService = ollamaService;
    }

    public String extractConcepts(
            Long materialId,
            String email
    ) {

        StudyMaterial material =
                studyMaterialRepository.findById(materialId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Study material not found"
                                )
                        );

        if (!material.getUser().getEmail().equals(email)) {
            throw new RuntimeException(
                    "You are not authorized to access this study material"
            );
        }

        String prompt = """
                You are RecallAI, a cognitive learning assistant.

                Analyze the following study material and identify the
                most important concepts a college student should learn.

                For each concept provide:
                1. Concept name
                2. Short explanation

                Return ONLY this format:

                Concept: <name>
                Explanation: <short explanation>

                Concept: <name>
                Explanation: <short explanation>

                Do not add introductions or conclusions.

                Subject:
                %s

                Study Material:
                %s
                """.formatted(
                material.getSubject(),
                material.getContent()
        );

        String aiResponse =
                ollamaService.generateResponse(prompt);

        saveExtractedConcepts(
                aiResponse,
                material.getSubject(),
                email
        );

        return aiResponse;
    }

    private void saveExtractedConcepts(
            String aiResponse,
            String subject,
            String email
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        String[] blocks = aiResponse.split("Concept:");

        for (String block : blocks) {

            if (block.isBlank()) {
                continue;
            }

            String[] parts = block.split(
                    "Explanation:",
                    2
            );

            if (parts.length < 2) {
                continue;
            }

            String name = parts[0].trim();
            String description = parts[1].trim();

            if (name.isEmpty() || description.isEmpty()) {
                continue;
            }

            Concept concept = new Concept(
                    name,
                    description,
                    subject,
                    user
            );

            conceptRepository.save(concept);
        }
    }
}