package com.recallai.backend.service;

import com.recallai.backend.model.StudyMaterial;
import com.recallai.backend.repository.StudyMaterialRepository;

import org.springframework.stereotype.Service;

@Service
public class QuizAIService {

    private final StudyMaterialRepository studyMaterialRepository;
    private final OllamaService ollamaService;

    public QuizAIService(
            StudyMaterialRepository studyMaterialRepository,
            OllamaService ollamaService
    ) {
        this.studyMaterialRepository = studyMaterialRepository;
        this.ollamaService = ollamaService;
    }

    public String generateQuiz(
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
                You are RecallAI, an adaptive learning assistant.

                Create a quiz based ONLY on the study material below.

                Generate 5 multiple-choice questions.

                For each question provide:

                Question: <question>
                A: <option>
                B: <option>
                C: <option>
                D: <option>
                Answer: <correct option>
                Explanation: <short explanation>

                Make the questions test understanding rather than
                simple memorization.

                Do not add information that is not present in the material.

                Subject:
                %s

                Study Material:
                %s
                """.formatted(
                material.getSubject(),
                material.getContent()
        );

        return ollamaService.generateResponse(prompt);
    }
}
