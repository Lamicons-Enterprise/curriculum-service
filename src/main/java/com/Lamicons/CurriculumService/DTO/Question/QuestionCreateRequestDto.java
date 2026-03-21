package com.Lamicons.CurriculumService.DTO.Question;

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
 * Request DTO for creating individual questions
 * Supports MCQ and Coding question types
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCreateRequestDto {
    
    @NotNull(message = "Question type is required")
    private QuestionType type;
    
    @NotBlank(message = "Question title is required")
    @Size(min = 5, max = 255)
    private String title;
    
    @Size(max = 4000)
    private String description;
    
    @Size(max = 100)
    private String topic;
    
    private Integer score;
    
    private Integer negativeScore;

    private String tags;

    // For MCQ questions
    private List<McqOptionDto> options;
    
    private String correctAnswer;
    
    // For Coding questions
    private Integer timeLimit;
    private Integer memoryLimit;
    private List<CodingTestCaseDto> testCases;
    private List<LanguageConfigDto> languageConfigs;
    
    // Optional: Assessment to link this question to
    private UUID assessmentId;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class McqOptionDto {
        private String optionText;
        private Boolean isCorrect;
    }
}
