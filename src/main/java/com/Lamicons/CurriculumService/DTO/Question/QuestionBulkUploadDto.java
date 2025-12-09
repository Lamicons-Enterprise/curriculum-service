package com.Lamicons.CurriculumService.DTO.Question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for bulk question upload via CSV
 * Each row in CSV will be mapped to this DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionBulkUploadDto {
    
    @NotNull(message = "Assessment ID is required")
    private UUID assessmentId;
    
    @NotNull(message = "Question type is required")
    private QuestionType type;
    
    @NotBlank(message = "Question title is required")
    private String title;
    
    private String description;
    
    private String topic;
    
    private Integer score;
    
    private Integer negativeScore;
    
    // For MCQ questions
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctOption; // A, B, C, or D
    
    // For coding questions
    private String sampleInput;
    private String sampleOutput;
    private String constraints;
    
    private Integer orderNumber;
}
