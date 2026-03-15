package com.Lamicons.CurriculumService.Repository;

import com.Lamicons.CurriculumService.DTO.Portfolio.PortfolioHighlightType;
import com.Lamicons.CurriculumService.Entity.Portfolio.PortfolioHighlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PortfolioHighlightRepository extends JpaRepository<PortfolioHighlight, UUID> {

    List<PortfolioHighlight> findByType(PortfolioHighlightType type);
}
