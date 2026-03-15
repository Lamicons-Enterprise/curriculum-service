package com.Lamicons.CurriculumService.Repository;

import com.Lamicons.CurriculumService.Entity.Question.CodingTestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CodingTestCaseRepository extends JpaRepository<CodingTestCase, UUID> {
    List<CodingTestCase> findByCodingQuestionIdOrderByOrderNumberAsc(UUID questionId);
}
