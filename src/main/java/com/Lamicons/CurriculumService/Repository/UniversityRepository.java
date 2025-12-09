package com.Lamicons.CurriculumService.Repository;

import com.Lamicons.CurriculumService.Entity.PhysicalEntity.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UniversityRepository extends JpaRepository<University, UUID> {
    
    Optional<University> findByUniversityCode(String universityCode);
    
    boolean existsByUniversityCode(String universityCode);
}
