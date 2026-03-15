package com.Lamicons.CurriculumService.DTO.Portfolio;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeaturedStudentResponseDto {

    private UUID id;
    private String name;
    private String courseCompleted;
    private String placementCompany;
    private String testimonial;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
