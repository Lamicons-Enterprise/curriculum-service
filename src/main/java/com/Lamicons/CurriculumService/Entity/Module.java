package com.Lamicons.CurriculumService.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "module")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Module {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @NotBlank(message = "Module title cannot be blank")
    @Size(min = 2, max = 255, message = "Module title must be between 2 and 255 characters")
    @Column(nullable = false)
    private String title;

    @Size(max = 4000, message = "Description cannot exceed 4000 characters")
    @Column(columnDefinition = "text")
    private String description;

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
    
    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ModuleContent> contents = new HashSet<>();
}
