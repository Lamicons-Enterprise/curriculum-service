package com.Lamicons.CurriculumService.Repository;

import com.Lamicons.CurriculumService.Entity.PhysicalEntity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BatchRepository extends JpaRepository<Batch, UUID> {
    
    Optional<Batch> findByBatchCode(String batchCode);
    
    boolean existsByBatchCode(String batchCode);
    
    List<Batch> findByCourseId(UUID courseId);
    
    List<Batch> findByUniversityId(UUID universityId);
}
