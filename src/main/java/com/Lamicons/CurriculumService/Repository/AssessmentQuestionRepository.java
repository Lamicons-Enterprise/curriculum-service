package com.Lamicons.CurriculumService.Repository;

import com.Lamicons.CurriculumService.Entity.JunctionTable.AssessmentQuestion;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssessmentQuestionRepository extends JpaRepository<AssessmentQuestion, UUID> {
    @Modifying
    @Transactional
    @Query("DELETE FROM AssessmentQuestion aq WHERE aq.question.id = :questionId")
    void deleteByQuestionId(@Param("questionId") UUID questionId);
    
    List<AssessmentQuestion> findByAssessment_IdOrderByOrderNumber(UUID assessmentId);
    
    // Additional methods for course builder workflow
    @Query("SELECT aq FROM AssessmentQuestion aq WHERE aq.assessment.id = :assessmentId ORDER BY aq.orderNumber ASC")
    List<AssessmentQuestion> findByAssessmentIdOrderByOrderNumber(@Param("assessmentId") UUID assessmentId);
    
    @Query("SELECT COUNT(aq) FROM AssessmentQuestion aq WHERE aq.assessment.id = :assessmentId")
    Long countByAssessmentId(@Param("assessmentId") UUID assessmentId);
}
