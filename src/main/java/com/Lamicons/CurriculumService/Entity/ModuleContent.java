package com.Lamicons.CurriculumService.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "module_content")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleContent {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private Module module;

    @NotNull(message = "Content type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    private ContentType contentType;

    @NotBlank(message = "Title cannot be blank")
    @Size(min = 2, max = 255, message = "Title must be between 2 and 255 characters")
    @Column(nullable = false)
    private String title;

    @Size(max = 4000, message = "Description cannot exceed 4000 characters")
    @Column(columnDefinition = "text")
    private String description;

    @Size(max = 1024, message = "URL cannot exceed 1024 characters")
    private String url;
    
    @Size(max = 1024, message = "Thumbnail URL cannot exceed 1024 characters")
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
    
    @Min(value = 0, message = "Estimated time cannot be negative")
    @Column(name = "estimated_time_minutes")
    @Builder.Default
    private Integer estimatedTimeMinutes = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty")
    @Builder.Default
    private Difficulty difficulty = Difficulty.BEGINNER;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "completion_status")
    @Builder.Default
    private CompletionStatus completionStatus = CompletionStatus.NOT_STARTED;

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

    public enum ContentType {
        video, article, pdf, assignment
    }
    
    public enum Difficulty {
        BEGINNER, INTERMEDIATE, ADVANCED
    }
    
    public enum CompletionStatus {
        NOT_STARTED, IN_PROGRESS, COMPLETED
    }
}