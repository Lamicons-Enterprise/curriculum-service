package com.Lamicons.CurriculumService.Entity;
import com.Lamicons.CurriculumService.DTO.Course.CourseCategory;
import com.Lamicons.CurriculumService.DTO.Course.CourseLevel;
import com.Lamicons.CurriculumService.DTO.Course.CourseStatus;
import com.Lamicons.CurriculumService.DTO.Course.CourseVisibility;
import com.Lamicons.CurriculumService.Entity.JunctionTable.CourseModule;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "course")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Course {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @NotBlank(message = "Course name cannot be blank")
    @Size(min = 2, max = 255, message = "Course name must be between 2 and 255 characters")
    @Column(nullable = false)
    private String name;

    @Size(max = 4000, message = "Description cannot exceed 4000 characters")
    @Column(columnDefinition = "text")
    private String description;
    
    @Size(max = 500, message = "Short description cannot exceed 500 characters")
    @Column(name = "short_description")
    private String shortDescription;
    
    @Column(name = "level")
    @Enumerated(EnumType.STRING)
    private CourseLevel level;
    
    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private CourseCategory category;
    
    @Size(max = 500)
    @Column(name = "banner_url")
    private String bannerUrl;
    
    @Size(max = 500)
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
    
    @Size(max = 500)
    @Column(name = "promo_video_url")
    private String promoVideoUrl;
    
    @Size(max = 500)
    @Column(name = "certificate_url")
    private String certificateUrl;
    
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    @Column(name = "rating")
    private Double rating;
    
    @Min(value = 0)
    @Column(name = "rating_count")
    @Builder.Default
    private Integer ratingCount = 0;
    
    @Min(value = 0)
    @Column(name = "enrollment_count")
    @Builder.Default
    private Integer enrollmentCount = 0;
    
    @Column(name = "visibility")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CourseVisibility visibility = CourseVisibility.DRAFT;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CourseStatus status = CourseStatus.DRAFT;
    
    @Column(name = "published_at")
    private Instant publishedAt;
    
    @Column(name = "last_modified_by")
    private UUID lastModifiedBy;

    @Column(name = "is_complete")
    @Builder.Default
    private Boolean isComplete = false;
    
    @Size(max = 1000)
    @Column(name = "target_audience", columnDefinition = "text")
    private String targetAudience;

    @Min(value = 0, message = "Duration cannot be negative")
    @Column(name = "duration_weeks")
    @Builder.Default
    private Integer durationWeeks = 0;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @Version
    private Integer version;
    
    // Relationship to modules via junction table (supports reusable modules)
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<CourseModule> courseModules = new HashSet<>();
}
