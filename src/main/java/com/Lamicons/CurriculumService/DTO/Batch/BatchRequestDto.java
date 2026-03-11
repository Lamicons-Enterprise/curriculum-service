package com.Lamicons.CurriculumService.DTO.Batch;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for creating or updating a batch.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchRequestDto {
    
    @NotBlank(message = "Batch code is required")
    @Size(min = 2, max = 50, message = "Batch code must be between 2 and 50 characters")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "Batch code must contain only uppercase letters, numbers, underscores, and hyphens")
    private String batchCode;
    
    @NotBlank(message = "Batch name is required")
    @Size(min = 3, max = 255, message = "Batch name must be between 3 and 255 characters")
    private String batchName;
    
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;
    
    @NotNull(message = "End date is required")
    private LocalDateTime endDate;
    
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 10000, message = "Capacity cannot exceed 10000")
    private Integer capacity;

    @NotNull(message = "Course ID is required")
    private UUID courseId;
    @NotNull(message = "University ID is required")
    private UUID universityId;
    
    @NotNull(message = "Instructor user ID is required")
    private UUID instructorUserId;
}
