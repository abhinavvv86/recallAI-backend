package com.recallai.backend.service;

import com.recallai.backend.model.StudyMaterial;
import com.recallai.backend.model.User;
import com.recallai.backend.repository.StudyMaterialRepository;
import com.recallai.backend.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyMaterialService {

    private final StudyMaterialRepository studyMaterialRepository;
    private final UserRepository userRepository;

    public StudyMaterialService(
            StudyMaterialRepository studyMaterialRepository,
            UserRepository userRepository) {
        this.studyMaterialRepository = studyMaterialRepository;
        this.userRepository = userRepository;
    }

    public StudyMaterial createMaterial(
            String title,
            String content,
            String subject,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudyMaterial material = new StudyMaterial(
                title,
                content,
                subject,
                user);

        return studyMaterialRepository.save(material);
    }

    public List<StudyMaterial> getUserMaterials(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return studyMaterialRepository.findByUserId(user.getId());
    }
}