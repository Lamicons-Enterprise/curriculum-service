package com.Lamicons.CurriculumService.DTO.Portfolio;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioHighlightResponseDto {

    private UUID id;
    private PortfolioHighlightType type;
    private String title;
    private String description;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
