package com.Lamicons.CurriculumService.DTO.Portfolio;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeaturedTrainerRequestDto {

    @NotBlank(message = "Name is required")
    private String name;

    private String designation;
    private String experience;
    private String skills;
    private String description;
    private String profileImageUrl;
}
