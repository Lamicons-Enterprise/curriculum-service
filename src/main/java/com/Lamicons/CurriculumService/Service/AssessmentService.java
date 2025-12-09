package com.Lamicons.CurriculumService.Service;

import com.Lamicons.CurriculumService.DTO.Assessment.*;

import java.util.List;
import java.util.UUID;

/**
 * Assessment Admin Service for production-grade course builder workflow
 * Supports: Standalone assessment creation, module linking, reusability
 */
public interface AssessmentService {
    
    /**
     * Create a standalone reusable assessment
     * Can be attached to multiple modules later
     */
    AssessmentResponseDto createStandaloneAssessment(AssessmentAttachmentRequestDto.NewAssessmentDto request);
    
    /**
     * Update assessment details
     */
    AssessmentResponseDto updateAssessment(UUID assessmentId, AssessmentAttachmentRequestDto.NewAssessmentDto request);
    
    /**
     * Get assessment by ID with all questions
     */
    AssessmentResponseDto getAssessmentById(UUID assessmentId);
    
    /**
     * List all reusable assessments (for dropdown in admin UI)
     * Filters: isReusable=true, isActive=true
     */
//    List<AssessmentSummaryDto> getAllReusableAssessments();
    
    /**
     * Search assessments by title, type, or tags
     */
//    List<AssessmentSummaryDto> searchAssessments(String searchTerm, AssessmentType type);
    
    /**
     * Get all assessments attached to a specific module
     */
    List<AssessmentResponseDto> getAssessmentsByModuleId(UUID moduleId);
    
    /**
     * Attach assessments to a module (existing or new)
     */
    void attachAssessmentsToModule(AssessmentAttachmentRequestDto request);
    
    /**
     * Remove an assessment from a module
     */
    void removeAssessmentFromModule(UUID moduleId, UUID assessmentId);
    
    /**
     * Reorder assessments within a module
     */
//    void reorderAssessments(UUID moduleId, List<UUID> assessmentIdsInOrder);
    
    /**
     * Delete assessment (soft delete)
     */
    void deleteAssessment(UUID assessmentId);
}
