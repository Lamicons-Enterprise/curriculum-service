package com.Lamicons.CurriculumService.DTO.Question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McqResponseDto {
    private UUID id;
    private String title;
    private String description;
    private String topic;
    private Map<String, String> options;
    private String correctOption;
    private Integer score;
    private Integer negativeScore;
    private QuestionType type;
    private Instant createdAt;
    private Instant updatedAt;
}