package com.Lamicons.CurriculumService.DTO.Portfolio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioHighlightRequestDto {

    @NotNull(message = "Type is required")
    private PortfolioHighlightType type;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private String imageUrl;
}
