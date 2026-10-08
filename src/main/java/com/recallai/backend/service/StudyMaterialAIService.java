package com.recallai.backend.service;

import com.recallai.backend.model.StudyMaterial;
import com.recallai.backend.repository.StudyMaterialRepository;

import org.springframework.stereotype.Service;

@Service
public class StudyMaterialAIService {

    private final StudyMaterialRepository studyMaterialRepository;
    private final OllamaService ollamaService;

    public StudyMaterialAIService(
            StudyMaterialRepository studyMaterialRepository,
            OllamaService ollamaService
    ) {
        this.studyMaterialRepository = studyMaterialRepository;
        this.ollamaService = ollamaService;
    }

    public String generateSummary(
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
                You are RecallAI, a personalized learning assistant.

                Summarize the following study material clearly for a college student.

                Requirements:
                - Identify the main topic.
                - Explain the important concepts.
                - Use simple and understandable language.
                - Keep important technical terms.
                - Use bullet points where useful.
                - Do not add information that is not present in the material.

                Subject:
                %s

                Title:
                %s

                Study Material:
                %s
                """.formatted(
                material.getSubject(),
                material.getTitle(),
                material.getContent()
        );

        return ollamaService.generateResponse(prompt);
    }
}
