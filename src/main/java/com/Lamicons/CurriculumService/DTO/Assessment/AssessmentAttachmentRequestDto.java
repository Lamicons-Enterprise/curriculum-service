package com.Lamicons.CurriculumService.DTO.Assessment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for attaching assessments to a module (Step 3 of admin flow)
 * Supports both creating new assignments and linking existing ones
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentAttachmentRequestDto {
    
    @NotNull(message = "Course ID is required")
    private UUID courseId;
    
    @NotNull(message = "Module ID is required")
    private UUID moduleId;
    
    // Option 1: Link existing assessments
    private List<UUID> existingAssessmentIds;
    
    // Option 2: Create and attach new assessments
    private List<NewAssessmentDto> newAssessments;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewAssessmentDto {
        @NotBlank(message = "Assessment title is required")
        @Size(min = 3, max = 255)
        private String title;
        
        @Size(max = 4000)
        private String description;
        
        @NotNull(message = "Assessment type is required")
        private AssessmentType type;
        
        private String tags;
        
        private Integer totalMarks;
        
        private Integer passMarks;
        
        private Integer durationMinutes;
        
        private Integer order;
        
        @Builder.Default
        private Boolean isReusable = true;
    }
}
