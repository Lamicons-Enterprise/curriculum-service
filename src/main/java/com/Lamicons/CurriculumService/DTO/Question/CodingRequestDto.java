package com.Lamicons.CurriculumService.DTO.Question;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodingRequestDto {
    @NotBlank(message = "Coding question title cannot be blank")
    @Size(min = 5, max = 255, message = "Title must be between 5 and 255 characters")
    private String title;
    
    @Size(max = 4000, message = "Description cannot exceed 4000 characters")
    private String description;
    
    @Size(max = 100, message = "Topic name cannot exceed 100 characters")
    private String topic;

    @NotEmpty(message = "At least one test case must be provided")
    @Size(min = 1, message = "At least one test case must be provided")
    private List<Map<String, Object>> testcases;

    @Min(value = 0, message = "Score must be a non-negative number")
    @Max(value = 100, message = "Score cannot exceed 100 points") 
    @Builder.Default
    private Integer score = 2;
    
    @Max(value = 0, message = "Negative score must be less than or equal to 0")
    @Builder.Default
    private Integer negativeScore = -1;
}
