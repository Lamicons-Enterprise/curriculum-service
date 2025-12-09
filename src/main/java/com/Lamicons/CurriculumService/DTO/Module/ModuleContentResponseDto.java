package com.Lamicons.CurriculumService.DTO.Module;

import com.Lamicons.CurriculumService.Entity.ModuleContent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleContentResponseDto {
    private UUID id;
    private ModuleContent.ContentType contentType;
    private String title;
    private String description;
    private String url;
    private String thumbnailUrl;
    private Integer durationMinutes;
    private Integer estimatedTimeMinutes;
    private Integer order;
    private Boolean isActive;
    private UUID moduleId;
    private ModuleContent.Difficulty difficulty;
    private ModuleContent.CompletionStatus completionStatus;
    private String prerequisites; // JSON string containing prerequisite content IDs
}