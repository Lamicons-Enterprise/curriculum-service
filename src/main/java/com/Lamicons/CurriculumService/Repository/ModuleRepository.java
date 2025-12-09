package com.Lamicons.CurriculumService.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.Lamicons.CurriculumService.Entity.Module;

import java.util.List;
import java.util.UUID;

@Repository
public interface ModuleRepository extends JpaRepository<Module, UUID> {
    
    // Note: To find modules by course, use CourseModuleRepository
    // with the junction table pattern for many-to-many relationships
    
    // Find active modules (all active modules are reusable)
    List<Module> findByIsActiveTrue();
}

