package com.Lamicons.CurriculumService.Repository;


import com.Lamicons.CurriculumService.Entity.Question.McqQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface McqQuestionRepository extends JpaRepository<McqQuestion, UUID> {
}
