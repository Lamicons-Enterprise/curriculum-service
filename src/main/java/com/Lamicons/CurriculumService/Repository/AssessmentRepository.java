package com.Lamicons.CurriculumService.Repository;

import com.Lamicons.CurriculumService.Entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, UUID> {
    
    // Note: Assessments are related to Modules via ModuleAssessment junction table
    // To find assessments by module, use ModuleAssessmentRepository
    // To find assessments by course, first get modules via CourseModule, then use ModuleAssessmentRepository
}

