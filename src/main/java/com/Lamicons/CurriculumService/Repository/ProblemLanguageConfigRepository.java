package com.Lamicons.CurriculumService.Repository;

import com.Lamicons.CurriculumService.Entity.Question.ProblemLanguageConfig;
import com.Lamicons.CurriculumService.DTO.Question.SupportedLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProblemLanguageConfigRepository extends JpaRepository<ProblemLanguageConfig, UUID> {
    List<ProblemLanguageConfig> findByCodingQuestionId(UUID questionId);
    Optional<ProblemLanguageConfig> findByCodingQuestionIdAndLanguage(UUID questionId, SupportedLanguage language);
}
