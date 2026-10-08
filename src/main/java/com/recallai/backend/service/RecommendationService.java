package com.recallai.backend.service;

import com.recallai.backend.model.Concept;
import com.recallai.backend.model.User;
import com.recallai.backend.repository.ConceptRepository;
import com.recallai.backend.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class RecommendationService {

    private final ConceptRepository conceptRepository;
    private final UserRepository userRepository;
    private final OllamaService ollamaService;

    public RecommendationService(
            ConceptRepository conceptRepository,
            UserRepository userRepository,
            OllamaService ollamaService
    ) {
        this.conceptRepository = conceptRepository;
        this.userRepository = userRepository;
        this.ollamaService = ollamaService;
    }

    public String generateRecommendation(
            String email,
            String subject
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        List<Concept> concepts =
                conceptRepository.findByUserIdAndSubject(
                        user.getId(),
                        subject
                );

        if (concepts.isEmpty()) {
            return "No concepts found for this subject yet. "
                    + "Study some material and generate concepts first.";
        }

        List<Concept> weakConcepts = concepts.stream()
                .filter(concept ->
                        concept.getMasteryLevel() < 60.0
                )
                .sorted(
                        Comparator.comparingDouble(
                                concept -> concept.getMasteryLevel()
                        )
                )
                .toList();

        if (weakConcepts.isEmpty()) {
            return "Excellent work! You currently have no weak "
                    + "concepts in " + subject
                    + ". Continue practicing to maintain your mastery.";
        }

        StringBuilder conceptData = new StringBuilder();

        for (Concept concept : weakConcepts) {

            conceptData.append("Concept: ")
                    .append(concept.getName())
                    .append("\nMastery: ")
                    .append(concept.getMasteryLevel())
                    .append("%\nDescription: ")
                    .append(concept.getDescription())
                    .append("\n\n");
        }

        String prompt = """
                You are RecallAI, a personalized learning assistant.

                Based on the student's weak concepts below, create a
                concise revision recommendation.

                Student subject:
                %s

                Weak concepts:
                %s

                Provide:
                1. Which concept should be studied first.
                2. Why it needs attention.
                3. What the student should revise.
                4. A suggested order for revising the remaining weak concepts.

                Keep the recommendation practical and easy to follow.
                """.formatted(
                subject,
                conceptData.toString()
        );

        return ollamaService.generateResponse(prompt);
    }
}