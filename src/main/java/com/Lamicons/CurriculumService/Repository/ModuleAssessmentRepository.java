package com.Lamicons.CurriculumService.Repository;

import com.Lamicons.CurriculumService.Entity.JunctionTable.ModuleAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ModuleAssessmentRepository extends JpaRepository<ModuleAssessment, UUID> {
    
    // Find all assessments for a specific module, ordered by assessment_order
    @Query("SELECT ma FROM ModuleAssessment ma WHERE ma.module.id = :moduleId AND ma.isActive = true ORDER BY ma.assessmentOrder ASC")
    List<ModuleAssessment> findByModuleIdOrderByAssessmentOrder(@Param("moduleId") UUID moduleId);
    
    // Check if an assessment is already attached to a module
    @Query("SELECT ma FROM ModuleAssessment ma WHERE ma.module.id = :moduleId AND ma.assessment.id = :assessmentId")
    Optional<ModuleAssessment> findByModuleIdAndAssessmentId(@Param("moduleId") UUID moduleId, @Param("assessmentId") UUID assessmentId);
    
    // Count assessments in a module
    @Query("SELECT COUNT(ma) FROM ModuleAssessment ma WHERE ma.module.id = :moduleId AND ma.isActive = true")
    Long countByModuleId(@Param("moduleId") UUID moduleId);
    
    // Find all modules using a specific assessment (for reusability tracking)
    @Query("SELECT ma FROM ModuleAssessment ma WHERE ma.assessment.id = :assessmentId AND ma.isActive = true")
    List<ModuleAssessment> findByAssessmentId(@Param("assessmentId") UUID assessmentId);
    
    // Get all assessments for a course (through course -> modules -> assessments)
    @Query("SELECT ma FROM ModuleAssessment ma " +
           "JOIN CourseModule cm ON cm.module.id = ma.module.id " +
           "WHERE cm.course.id = :courseId AND ma.isActive = true AND cm.isActive = true " +
           "ORDER BY cm.moduleOrder, ma.assessmentOrder")
    List<ModuleAssessment> findByCourseId(@Param("courseId") UUID courseId);
}
