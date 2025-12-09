package com.Lamicons.CurriculumService.DTO.Batch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for batch response data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchResponseDto {

    private UUID id;

    // Batch info
    private String batchCode;
    private String batchName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer capacity;
    private Boolean active;

    // Minimal course info
    private UUID courseId;
    private String courseName;

    // Minimal university info
    private UUID universityId;
    private String universityName;
    
    // Audit fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID createdBy;
    private UUID updatedBy;
}
