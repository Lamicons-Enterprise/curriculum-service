package com.Lamicons.CurriculumService.DTO.Course;

import com.Lamicons.CurriculumService.DTO.Module.ModuleSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Complete course hierarchy response for admin review
 * Shows full nested structure: Course -> Modules -> Assignments -> Questions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseHierarchyResponseDto {
    private UUID courseId;
    private String name;
    private String description;
    private CourseStatus status;
    private CourseLevel level;
    private CourseCategory category;
    private Boolean isComplete;
    private Integer totalModules;
    private Integer totalAssessments;
    private Integer totalQuestions;
    private Instant createdAt;
    private Instant publishedAt;
    private List<ModuleSummaryDto> modules;
}
