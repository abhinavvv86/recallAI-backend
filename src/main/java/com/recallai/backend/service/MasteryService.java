package com.recallai.backend.service;

import com.recallai.backend.model.Concept;
import com.recallai.backend.model.User;
import com.recallai.backend.repository.ConceptRepository;
import com.recallai.backend.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MasteryService {

    private final ConceptRepository conceptRepository;
    private final UserRepository userRepository;

    public MasteryService(
            ConceptRepository conceptRepository,
            UserRepository userRepository
    ) {
        this.conceptRepository = conceptRepository;
        this.userRepository = userRepository;
    }

    public List<Concept> updateMastery(
            String email,
            String subject,
            double quizScore
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

        for (Concept concept : concepts) {

            double oldMastery = concept.getMasteryLevel();

            double newMastery =
                    (oldMastery * 0.7) +
                    (quizScore * 0.3);

            concept.setMasteryLevel(
                    Math.min(100.0, Math.max(0.0, newMastery))
            );

            conceptRepository.save(concept);
        }

        return concepts;
    }

    public List<Concept> getWeakConcepts(
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

        return concepts.stream()
                .filter(concept ->
                        concept.getMasteryLevel() < 60.0
                )
                .toList();
    }
}
