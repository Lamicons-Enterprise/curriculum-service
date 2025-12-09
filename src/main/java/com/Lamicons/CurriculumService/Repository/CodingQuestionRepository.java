package com.Lamicons.CurriculumService.Repository;

import com.Lamicons.CurriculumService.Entity.Question.CodingQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface CodingQuestionRepository extends JpaRepository<CodingQuestion, UUID> {
}