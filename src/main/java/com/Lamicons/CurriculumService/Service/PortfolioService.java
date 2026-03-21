package com.Lamicons.CurriculumService.Service;

import com.Lamicons.CurriculumService.DTO.Portfolio.*;

import java.util.List;
import java.util.UUID;

public interface PortfolioService {

    PortfolioSummaryResponseDto getFullPortfolio();

    OrganisationDetailResponseDto getOrganisationDetails();

    OrganisationDetailResponseDto createOrUpdateOrganisationDetails(OrganisationDetailRequestDto requestDto);

    List<FeaturedTrainerResponseDto> getAllTrainers();

    FeaturedTrainerResponseDto getTrainerById(UUID id);

    FeaturedTrainerResponseDto createTrainer(FeaturedTrainerRequestDto requestDto);

    FeaturedTrainerResponseDto updateTrainer(UUID id, FeaturedTrainerRequestDto requestDto);

    void deleteTrainer(UUID id);

    List<FeaturedStudentResponseDto> getAllStudents();

    FeaturedStudentResponseDto getStudentById(UUID id);

    FeaturedStudentResponseDto createStudent(FeaturedStudentRequestDto requestDto);

    FeaturedStudentResponseDto updateStudent(UUID id, FeaturedStudentRequestDto requestDto);

    void deleteStudent(UUID id);

    List<PortfolioHighlightResponseDto> getHighlightsByType(PortfolioHighlightType type);

    PortfolioHighlightResponseDto getHighlightById(UUID id);

    PortfolioHighlightResponseDto createHighlight(PortfolioHighlightRequestDto requestDto);

    PortfolioHighlightResponseDto updateHighlight(UUID id, PortfolioHighlightRequestDto requestDto);

    void deleteHighlight(UUID id);
}
