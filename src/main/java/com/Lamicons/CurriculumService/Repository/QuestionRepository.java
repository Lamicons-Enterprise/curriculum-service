package com.Lamicons.CurriculumService.Repository;

import com.Lamicons.CurriculumService.Entity.Question.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {
}
