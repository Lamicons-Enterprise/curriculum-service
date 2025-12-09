package com.Lamicons.CurriculumService.DTO.Module;

import com.Lamicons.CurriculumService.Entity.ModuleContent;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleContentRequestDto {
    @NotNull(message = "Content type is required")
    private ModuleContent.ContentType contentType;
    
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;
    
    @Size(max = 4000, message = "Description cannot exceed 4000 characters")
    private String description;
    
    @Size(max = 1024, message = "URL cannot exceed 1024 characters")
    private String url;
    
    @Size(max = 1024, message = "Thumbnail URL cannot exceed 1024 characters")
    private String thumbnailUrl;
    
    @Min(value = 0, message = "Duration minutes cannot be negative")
    private Integer durationMinutes;
    
    @Min(value = 0, message = "Estimated time cannot be negative")
    private Integer estimatedTimeMinutes;
    
    @Min(value = 0, message = "Order cannot be negative")
    private Integer order;
    
    private Boolean isActive;
    
    private ModuleContent.Difficulty difficulty;
}