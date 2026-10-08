package com.recallai.backend.service;

import com.recallai.backend.model.Concept;
import com.recallai.backend.model.User;
import com.recallai.backend.repository.ConceptRepository;
import com.recallai.backend.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConceptService {

    private final ConceptRepository conceptRepository;
    private final UserRepository userRepository;

    public ConceptService(
            ConceptRepository conceptRepository,
            UserRepository userRepository
    ) {
        this.conceptRepository = conceptRepository;
        this.userRepository = userRepository;
    }

    public Concept createConcept(
            String name,
            String description,
            String subject,
            String email
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        Concept concept = new Concept(
                name,
                description,
                subject,
                user
        );

        return conceptRepository.save(concept);
    }

    public List<Concept> getUserConcepts(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        return conceptRepository.findByUserId(user.getId());
    }

    public List<Concept> getUserConceptsBySubject(
            String email,
            String subject
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        return conceptRepository.findByUserIdAndSubject(
                user.getId(),
                subject
        );
    }
}
