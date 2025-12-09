package com.Lamicons.CurriculumService.Entity.JunctionTable;

import com.Lamicons.CurriculumService.Entity.Assessment;
import com.Lamicons.CurriculumService.Entity.Module;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Junction table to support many-to-many relationship between Module and Assessment
 * Enables assignment reusability across multiple modules
 * Tracks assignment order within each specific module
 */
@Entity
@Table(
    name = "module_assessment",
    uniqueConstraints = @UniqueConstraint(columnNames = {"module_id", "assessment_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleAssessment {
    
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;
    
    // Order of this assessment within the specific module
    @Min(value = 0, message = "Order cannot be negative")
    @Column(name = "assessment_order")
    @Builder.Default
    private Integer assessmentOrder = 0;
    
    // Track when this assessment was added to the module
    @Column(name = "added_at")
    @Builder.Default
    private Instant addedAt = Instant.now();
    
    // Optional: admin who added this assessment to the module
    @Column(name = "added_by")
    private UUID addedBy;
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
    
    // Optional: Override assessment configuration for this specific module
    @Column(name = "custom_total_marks")
    private Integer customTotalMarks;
    
    @Column(name = "custom_pass_marks")
    private Integer customPassMarks;
    
    @Column(name = "custom_duration_minutes")
    private Integer customDurationMinutes;
}
