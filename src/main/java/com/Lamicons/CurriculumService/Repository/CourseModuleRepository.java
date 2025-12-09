package com.Lamicons.CurriculumService.Repository;

import com.Lamicons.CurriculumService.Entity.JunctionTable.CourseModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseModuleRepository extends JpaRepository<CourseModule, UUID> {
    
    // Find all modules for a specific course, ordered by module_order
    @Query("SELECT cm FROM CourseModule cm WHERE cm.course.id = :courseId AND cm.isActive = true ORDER BY cm.moduleOrder ASC")
    List<CourseModule> findByCourseIdOrderByModuleOrder(@Param("courseId") UUID courseId);
    
    // Check if a module is already attached to a course
    @Query("SELECT cm FROM CourseModule cm WHERE cm.course.id = :courseId AND cm.module.id = :moduleId")
    Optional<CourseModule> findByCourseIdAndModuleId(@Param("courseId") UUID courseId, @Param("moduleId") UUID moduleId);
    
    // Count modules in a course
    @Query("SELECT COUNT(cm) FROM CourseModule cm WHERE cm.course.id = :courseId AND cm.isActive = true")
    Long countByCourseId(@Param("courseId") UUID courseId);
    
    // Find all courses using a specific module (for reusability tracking)
    @Query("SELECT cm FROM CourseModule cm WHERE cm.module.id = :moduleId AND cm.isActive = true")
    List<CourseModule> findByModuleId(@Param("moduleId") UUID moduleId);
}
