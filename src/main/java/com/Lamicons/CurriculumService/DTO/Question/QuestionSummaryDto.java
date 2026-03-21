package com.Lamicons.CurriculumService.DTO.Question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Question summary for assessment hierarchy view
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionSummaryDto {
    private UUID questionId;
    private QuestionType type;
    private String title;
    private String description;
    private String topic;
    private Integer score;
    private Integer negativeScore;
    private Integer orderNumber;
    
    // MCQ specific fields
    private Map<String, String> options;
    private List<String> correctOption;
    
    // Coding specific fields
    private List<CodingTestCaseDto> testCases;
    private Integer timeLimit;
    private Integer memoryLimit;
    private List<LanguageConfigDto> languageConfigs;
}
