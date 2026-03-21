package com.Lamicons.CurriculumService.DTO.Portfolio;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeaturedStudentRequestDto {

    @NotBlank(message = "Name is required")
    private String name;

    private String courseCompleted;
    private String placementCompany;
    private String testimonial;
    private String profileImageUrl;
}
