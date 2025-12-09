package com.Lamicons.CurriculumService.DTO.Course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a course (Step 1 of admin flow)
 * Creates course in DRAFT status with minimal required fields
 * Admin can skip and add modules later
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseCreateRequestDto {
    
    @NotBlank(message = "Course name is required")
    @Size(min = 2, max = 255, message = "Course name must be between 2 and 255 characters")
    private String name;
    
    @Size(max = 4000, message = "Description cannot exceed 4000 characters")
    private String description;
    
    @Size(max = 500, message = "Short description cannot exceed 500 characters")
    private String shortDescription;
    
    private CourseLevel level;
    
    private CourseCategory category;
    
    @Size(max = 500)
    private String bannerUrl;
    
    @Size(max = 500)
    private String thumbnailUrl;
    
    @Size(max = 500)
    private String promoVideoUrl;
    
    @Size(max = 1000)
    private String targetAudience;

    private Integer durationWeeks;
}
