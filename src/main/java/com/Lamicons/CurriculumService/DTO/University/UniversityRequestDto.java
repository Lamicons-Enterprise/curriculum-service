package com.Lamicons.CurriculumService.DTO.University;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO for creating or updating a university.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UniversityRequestDto {
    
    @NotBlank(message = "University code is required")
    @Size(min = 2, max = 20, message = "University code must be between 2 and 20 characters")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "University code must contain only uppercase letters, numbers, underscores, and hyphens")
    private String universityCode;
    
    @NotBlank(message = "University name is required")
    @Size(min = 3, max = 255, message = "University name must be between 3 and 255 characters")
    private String name;
    
    @Size(max = 100, message = "City name cannot exceed 100 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State name cannot exceed 100 characters")
    private String state;
    
    @Size(max = 100, message = "Country name cannot exceed 100 characters")
    private String country;
    
    private Boolean active;
}
