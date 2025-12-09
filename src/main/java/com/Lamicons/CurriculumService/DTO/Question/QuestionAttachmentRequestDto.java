package com.Lamicons.CurriculumService.DTO.Question;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for adding questions to an assessment (Step 4 of admin flow)
 * Supports: 1) Linking existing questions, 2) Creating new questions, 3) Bulk CSV upload
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionAttachmentRequestDto {
    
    @NotNull(message = "Course ID is required")
    private UUID courseId;
    
    @NotNull(message = "Module ID is required")
    private UUID moduleId;
    
    @NotNull(message = "Assessment ID is required")
    private UUID assessmentId;
    
    // Option 1: Link existing questions from question bank
    private List<UUID> existingQuestionIds;
    
    // Option 2: Create new questions inline
    // (Note: For actual question creation, use dedicated QuestionCreateRequestDto)
    // This is just for linking - creation handled by QuestionController
    
    // Option 3: CSV bulk upload is handled by separate endpoint
}
