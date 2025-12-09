package com.Lamicons.CurriculumService.Repository;

import com.Lamicons.CurriculumService.Entity.ModuleContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ModuleContentRepository extends JpaRepository<ModuleContent, UUID> {
    List<ModuleContent> findByModuleId(UUID moduleId);
    List<ModuleContent> findByModuleIdOrderByOrder(UUID moduleId);
    void deleteByModuleId(UUID moduleId);
}