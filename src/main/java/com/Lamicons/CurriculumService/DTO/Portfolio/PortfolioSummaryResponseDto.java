package com.Lamicons.CurriculumService.DTO.Portfolio;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioSummaryResponseDto {

    private OrganisationDetailResponseDto organisationDetails;
    private List<FeaturedTrainerResponseDto> featuredTrainers;
    private List<FeaturedStudentResponseDto> featuredStudents;
    private List<PortfolioHighlightResponseDto> achievements;
    private List<PortfolioHighlightResponseDto> majorClients;
    private List<PortfolioHighlightResponseDto> universityPartners;
}
