package com.recallai.backend.repository;

import com.recallai.backend.model.StudyMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyMaterialRepository
        extends JpaRepository<StudyMaterial, Long> {

    List<StudyMaterial> findByUserId(Long userId);
}