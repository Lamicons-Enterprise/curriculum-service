package com.Lamicons.CurriculumService.Entity.JunctionTable;

import com.Lamicons.CurriculumService.Entity.Course;
import com.Lamicons.CurriculumService.Entity.Module;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Junction table to support many-to-many relationship between Course and Module
 * Enables module reusability across multiple courses (Coursera/Udemy pattern)
 * Tracks module order within each specific course
 */
@Entity
@Table(
    name = "course_module",
    uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "module_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseModule {
    
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;
    
    // Order of this module within the specific course
    @Min(value = 0, message = "Order cannot be negative")
    @Column(name = "module_order")
    @Builder.Default
    private Integer moduleOrder = 0;
    
    // Track when this module was added to the course
    @Column(name = "added_at")
    @Builder.Default
    private Instant addedAt = Instant.now();
    
    // Optional: admin who added this module to the course
    @Column(name = "added_by")
    private UUID addedBy;
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}
