package com.recallai.backend.service;

import com.recallai.backend.model.QuizResult;
import com.recallai.backend.model.User;
import com.recallai.backend.repository.QuizResultRepository;
import com.recallai.backend.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizResultService {

    private final QuizResultRepository quizResultRepository;
    private final UserRepository userRepository;

    public QuizResultService(
            QuizResultRepository quizResultRepository,
            UserRepository userRepository
    ) {
        this.quizResultRepository = quizResultRepository;
        this.userRepository = userRepository;
    }

    public QuizResult saveResult(
            int totalQuestions,
            int correctAnswers,
            String subject,
            String email
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        QuizResult result = new QuizResult(
                totalQuestions,
                correctAnswers,
                subject,
                user
        );

        return quizResultRepository.save(result);
    }

    public List<QuizResult> getUserResults(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        return quizResultRepository.findByUserId(
                user.getId()
        );
    }

    public List<QuizResult> getUserResultsBySubject(
            String email,
            String subject
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        return quizResultRepository.findByUserIdAndSubject(
                user.getId(),
                subject
        );
    }
}