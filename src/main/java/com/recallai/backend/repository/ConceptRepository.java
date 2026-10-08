package com.recallai.backend.repository;

import com.recallai.backend.model.Concept;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConceptRepository
        extends JpaRepository<Concept, Long> {

    List<Concept> findByUserId(Long userId);

    List<Concept> findByUserIdAndSubject(Long userId, String subject);
}