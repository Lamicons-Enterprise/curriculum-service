package com.Lamicons.CurriculumService.DTO.Question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodingResponseDto {
    private UUID id;
    private String title;
    private String description;
    private String topic;
    private Integer timeLimit;
    private Integer memoryLimit;
    private Integer score;
    private Integer negativeScore;
    private QuestionType type;
    private Instant createdAt;
    private Instant updatedAt;
}