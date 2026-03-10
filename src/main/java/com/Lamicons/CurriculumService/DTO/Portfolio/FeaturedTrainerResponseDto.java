package com.Lamicons.CurriculumService.DTO.Portfolio;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeaturedTrainerResponseDto {

    private UUID id;
    private String name;
    private String designation;
    private String experience;
    private String skills;
    private String description;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
