package com.Lamicons.CurriculumService.DTO.Assessment;

import com.Lamicons.CurriculumService.DTO.Question.QuestionCreateRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;
import java.util.UUID;

/**
 * DTO for creating an assessment specific to a batch, not tied to a course or module
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchAssessmentRequestDto {

    @NotBlank(message = "Assessment title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;
    
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;
    
    @NotNull(message = "Assessment type is required")
    private AssessmentType type;
    
    @Min(value = 0, message = "Total marks cannot be negative")
    private Integer totalMarks;
    
    @Min(value = 0, message = "Pass marks cannot be negative")
    private Integer passMarks;
    
    @Min(value = 0, message = "Duration minutes cannot be negative")
    private Integer durationMinutes;
    
    @Min(value = 0, message = "Order cannot be negative")
    private Integer order;
    
    private Boolean isActive;

    // Select existing questions
    private List<UUID> existingQuestionIds;

    // Add new questions inline
    @Valid
    private List<QuestionCreateRequestDto> newQuestions;
}