package com.recallai.backend.service;

import com.recallai.backend.model.Concept;
import com.recallai.backend.model.QuizResult;
import com.recallai.backend.model.StudyMaterial;
import com.recallai.backend.model.User;
import com.recallai.backend.repository.ConceptRepository;
import com.recallai.backend.repository.QuizResultRepository;
import com.recallai.backend.repository.StudyMaterialRepository;
import com.recallai.backend.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final StudyMaterialRepository studyMaterialRepository;
    private final ConceptRepository conceptRepository;
    private final QuizResultRepository quizResultRepository;

    public DashboardService(
            UserRepository userRepository,
            StudyMaterialRepository studyMaterialRepository,
            ConceptRepository conceptRepository,
            QuizResultRepository quizResultRepository
    ) {
        this.userRepository = userRepository;
        this.studyMaterialRepository = studyMaterialRepository;
        this.conceptRepository = conceptRepository;
        this.quizResultRepository = quizResultRepository;
    }

    public DashboardData getDashboard(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        List<StudyMaterial> materials =
                studyMaterialRepository.findByUserId(
                        user.getId()
                );

        List<Concept> concepts =
                conceptRepository.findByUserId(
                        user.getId()
                );

        List<QuizResult> quizResults =
                quizResultRepository.findByUserId(
                        user.getId()
                );

        double totalQuizScore = 0.0;

        for (QuizResult result : quizResults) {
            totalQuizScore += result.getScore();
        }

        double averageScore = 0.0;

        if (!quizResults.isEmpty()) {
            averageScore =
                    totalQuizScore / quizResults.size();
        }

        long weakConceptCount = 0;

        double totalMastery = 0.0;

        for (Concept concept : concepts) {

            double mastery =
                    concept.getMasteryLevel();

            totalMastery += mastery;

            if (mastery < 60.0) {
                weakConceptCount++;
            }
        }

        double averageMastery = 0.0;

        if (!concepts.isEmpty()) {
            averageMastery =
                    totalMastery / concepts.size();
        }

        return new DashboardData(
                user.getName(),
                user.getEmail(),
                materials.size(),
                concepts.size(),
                quizResults.size(),
                weakConceptCount,
                averageMastery,
                averageScore,
                concepts,
                quizResults
        );
    }

    public static class DashboardData {

        private String name;
        private String email;

        private int totalMaterials;
        private int totalConcepts;
        private int totalQuizzes;

        private long weakConcepts;

        private double averageMastery;
        private double averageQuizScore;

        private List<Concept> concepts;
        private List<QuizResult> quizResults;

        public DashboardData(
                String name,
                String email,
                int totalMaterials,
                int totalConcepts,
                int totalQuizzes,
                long weakConcepts,
                double averageMastery,
                double averageQuizScore,
                List<Concept> concepts,
                List<QuizResult> quizResults
        ) {
            this.name = name;
            this.email = email;
            this.totalMaterials = totalMaterials;
            this.totalConcepts = totalConcepts;
            this.totalQuizzes = totalQuizzes;
            this.weakConcepts = weakConcepts;
            this.averageMastery = averageMastery;
            this.averageQuizScore = averageQuizScore;
            this.concepts = concepts;
            this.quizResults = quizResults;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public int getTotalMaterials() {
            return totalMaterials;
        }

        public int getTotalConcepts() {
            return totalConcepts;
        }

        public int getTotalQuizzes() {
            return totalQuizzes;
        }

        public long getWeakConcepts() {
            return weakConcepts;
        }

        public double getAverageMastery() {
            return averageMastery;
        }

        public double getAverageQuizScore() {
            return averageQuizScore;
        }

        public List<Concept> getConcepts() {
            return concepts;
        }

        public List<QuizResult> getQuizResults() {
            return quizResults;
        }
    }
}