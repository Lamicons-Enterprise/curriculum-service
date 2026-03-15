package com.Lamicons.CurriculumService.Repository;

import com.Lamicons.CurriculumService.Entity.Portfolio.OrganisationDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrganisationDetailRepository extends JpaRepository<OrganisationDetail, UUID> {
}
