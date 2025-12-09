package com.Lamicons.CurriculumService.Entity;

import com.Lamicons.CurriculumService.DTO.Assessment.AssessmentType;
import com.Lamicons.CurriculumService.Entity.PhysicalEntity.Batch;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "assessment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assessment {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @NotBlank(message = "Assessment title cannot be blank")
    @Size(min = 3, max = 255, message = "Assessment title must be between 3 and 255 characters")
    @Column(nullable = false)
    private String title;

    @Size(max = 4000, message = "Description cannot exceed 4000 characters")
    @Column(columnDefinition = "text")
    private String description;

    @NotNull(message = "Assessment type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private AssessmentType type;
    
    @Min(value = 0, message = "Total marks cannot be negative")
    @Column(name = "total_marks")
    @Builder.Default
    private Integer totalMarks = 0;
    
    @Min(value = 0, message = "Pass marks cannot be negative")
    @Column(name = "pass_marks")
    @Builder.Default
    private Integer passMarks = 0;
    
    @Min(value = 0, message = "Duration cannot be negative")
    @Column(name = "duration_minutes")
    @Builder.Default
    private Integer durationMinutes = 0;
    
    @Min(value = 0, message = "Order cannot be negative")
    @Column(name = "\"order\"")
    @Builder.Default
    private Integer order = 0;
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @Version
    private Integer version;

}

