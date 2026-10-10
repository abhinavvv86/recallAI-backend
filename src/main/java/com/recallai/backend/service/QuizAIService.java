
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

    public String generateQuiz(Long materialId, String email) {

        StudyMaterial material = studyMaterialRepository
                .findById(materialId)
                .orElseThrow(() ->
                        new RuntimeException("Study material not found")
                );

        if (material.getUser() == null
                || !material.getUser().getEmail().equals(email)) {
            throw new RuntimeException(
                    "You are not authorized to access this study material"
            );
        }

        String prompt = """
                You are RecallAI, a quiz generator.

                Create exactly 5 multiple-choice questions using ONLY
                the study material provided below.

                IMPORTANT OUTPUT RULES:
                - Do not greet the user.
                - Do not write an introduction or conclusion.
                - Do not use Markdown code fences.
                - Follow the exact format shown below.
                - Number every question from 1 through 5.
                - Each question must have exactly four options: A, B, C, D.
                - The Answer must be a single option letter: A, B, C, or D.
                - Include a short explanation for each correct answer.
                - Do not omit any required line.
                - Do not invent facts that are absent from the material.

                REQUIRED FORMAT:

                Question 1: Write the first question here
                A: First option
                B: Second option
                C: Third option
                D: Fourth option
                Answer: A
                Explanation: Explain why A is correct.

                Question 2: Write the second question here
                A: First option
                B: Second option
                C: Third option
                D: Fourth option
                Answer: B
                Explanation: Explain why B is correct.

                Continue with the same format for Question 3,
                Question 4, and Question 5.

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
