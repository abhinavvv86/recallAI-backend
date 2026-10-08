package com.recallai.backend.repository;

import com.recallai.backend.model.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizResultRepository
        extends JpaRepository<QuizResult, Long> {

    List<QuizResult> findByUserId(Long userId);

    List<QuizResult> findByUserIdAndSubject(
            Long userId,
            String subject
    );
}
