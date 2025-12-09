package com.Lamicons.CurriculumService.DTO.Assessment;

import com.Lamicons.CurriculumService.DTO.Question.QuestionSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResponseDto {
    private UUID assessmentId;
    private String title;
    private String description;
    private AssessmentType type;
    private Integer totalMarks;
    private Integer passMarks;
    private Integer durationMinutes;
    private Integer assessmentOrder;
    private Integer totalQuestions;
    private Boolean isActive;
    private List<QuestionSummaryDto> questions;
}
