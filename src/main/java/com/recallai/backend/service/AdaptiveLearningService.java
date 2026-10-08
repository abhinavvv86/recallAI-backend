package com.recallai.backend.service;

import com.recallai.backend.model.Concept;
import com.recallai.backend.model.QuizResult;
import com.recallai.backend.repository.ConceptRepository;
import com.recallai.backend.repository.QuizResultRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class AdaptiveLearningService {

    private final ConceptRepository conceptRepository;
    private final QuizResultRepository quizResultRepository;
    private final OllamaService ollamaService;
    private final UserService userService;

    public AdaptiveLearningService(
            ConceptRepository conceptRepository,
            QuizResultRepository quizResultRepository,
            OllamaService ollamaService,
            UserService userService
    ) {
        this.conceptRepository = conceptRepository;
        this.quizResultRepository = quizResultRepository;
        this.ollamaService = ollamaService;
        this.userService = userService;
    }

    public String generateNextStudyAction(
            String email,
            String subject
    ) {

        Long userId =
                userService.findByEmail(email).getId();

        List<Concept> concepts =
                conceptRepository.findByUserIdAndSubject(
                        userId,
                        subject
                );

        List<QuizResult> quizResults =
                quizResultRepository.findByUserIdAndSubject(
                        userId,
                        subject
                );

        if (concepts.isEmpty()) {
            return "No concepts are available for this subject yet. Add study material and extract concepts first.";
        }

        List<Concept> weakestConcepts =
                concepts.stream()
                        .sorted(
        Comparator.comparingDouble(
                concept -> concept.getMasteryLevel()
        )
)
                        .limit(5)
                        .toList();

        StringBuilder conceptData =
                new StringBuilder();

        for (Concept concept : weakestConcepts) {

            conceptData.append(
                    "Concept: "
            ).append(
                    concept.getName()
            ).append(
                    "\nMastery: "
            ).append(
                    String.format(
                            "%.1f",
                            concept.getMasteryLevel()
                    )
            ).append(
                    "%\nDescription: "
            ).append(
                    concept.getDescription()
            ).append(
                    "\n\n"
            );
        }

        StringBuilder quizData =
                new StringBuilder();

        for (QuizResult result : quizResults) {

            quizData.append(
                    "Quiz score: "
            ).append(
                    String.format(
                            "%.1f",
                            result.getScore()
                    )
            ).append(
                    "%, Correct: "
            ).append(
                    result.getCorrectAnswers()
            ).append(
                    "/"
            ).append(
                    result.getTotalQuestions()
            ).append(
                    "\n"
            );
        }

        String prompt =
                """
                You are RecallAI, an adaptive learning assistant.

                Create the student's next best study action.

                Subject:
                %s

                Weakest concepts:
                %s

                Recent quiz performance:
                %s

                Give a concise personalized learning plan.

                Your response must contain:

                1. NEXT CONCEPT
                Choose the single most important concept to study first.

                2. WHY
                Explain why this concept should be studied now.

                3. STUDY ACTION
                Give one practical action the student should perform.

                4. PRACTICE
                Suggest one practice activity.

                5. TARGET
                Give a realistic mastery target.

                Do not give generic motivational advice.
                Base the recommendation on the mastery levels
                and quiz performance provided.
                """
                .formatted(
                        subject,
                        conceptData,
                        quizData
                );

        return ollamaService.generateResponse(prompt);
    }
}