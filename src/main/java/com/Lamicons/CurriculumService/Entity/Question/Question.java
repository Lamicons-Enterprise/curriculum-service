package com.Lamicons.CurriculumService.Entity.Question;

import com.Lamicons.CurriculumService.DTO.Question.QuestionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)  // Joined tables
@Entity
@Table(name = "question")
@SuperBuilder
public abstract class Question {

    @Id
    @GeneratedValue
    private UUID id;

    @NotNull(message = "Question type cannot be null")
    @Enumerated(EnumType.STRING)
    private QuestionType type;

    @NotBlank(message = "Question title cannot be blank")
    @Size(min = 5, max = 255, message = "Title must be between 5 and 255 characters")
    @Column(nullable = false, length = 255)
    private String title;
    
    @Size(max = 4000, message = "Description cannot exceed 4000 characters")
    @Column(columnDefinition = "text")
    private String description;
    
    @Size(max = 100, message = "Topic name cannot exceed 100 characters")
    @Column(length = 100)
    private String topic;

    @Min(value = 0, message = "Score must be a non-negative number")
    @Max(value = 1000, message = "Score cannot exceed 1000 points")
    private Integer score;

    @Max(value = 0, message = "Negative score must be less than or equal to 0")
    private Integer negativeScore;

    @Size(max = 500, message = "Tags cannot exceed 500 characters")
    @Column(length = 500)
    private String tags;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();
}

