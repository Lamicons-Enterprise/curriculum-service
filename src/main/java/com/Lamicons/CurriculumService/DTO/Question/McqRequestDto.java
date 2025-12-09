package com.Lamicons.CurriculumService.DTO.Question;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class McqRequestDto {
    @NotBlank(message = "Question title cannot be blank")
    @Size(min = 5, max = 255, message = "Title must be between 5 and 255 characters")
    private String title;
    
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;
    
    @Size(max = 100, message = "Topic name cannot exceed 100 characters")
    private String topic;
    
    @NotEmpty(message = "Options cannot be empty - at least two options must be provided")
    @Size(min = 2, message = "At least two options must be provided for an MCQ")
    private Map<String, String> options;
    
    @NotBlank(message = "Correct option identifier cannot be blank")
    private String correctOption;
    
    @Min(value = 0, message = "Score must be a non-negative number")
    @Max(value = 100, message = "Score cannot exceed 100 points")
    @Builder.Default
    private Integer score = 2;
    
    @Max(value = 0, message = "Negative score must be less than or equal to 0")
    @Builder.Default
    private Integer negativeScore = -1;
}
