package com.Lamicons.CurriculumService.DTO.Module;

import com.Lamicons.CurriculumService.DTO.Assessment.AssessmentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Module summary with nested assessments for course hierarchy view
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleSummaryDto {
    private UUID moduleId;
    private String title;
    private String description;
    private Integer moduleOrder;
    private Integer totalAssessments;
    private Integer totalQuestions;
    private Boolean isActive;
    private List<AssessmentResponseDto> assessments;
}
