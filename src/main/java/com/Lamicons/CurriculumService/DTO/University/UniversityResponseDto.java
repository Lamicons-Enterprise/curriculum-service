package com.Lamicons.CurriculumService.DTO.University;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for university response data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UniversityResponseDto {
    
    private UUID id;
    private String universityCode;
    private String name;
    private String city;
    private String state;
    private String country;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID createdBy;
    private UUID updatedBy;
}
