package com.Lamicons.CurriculumService.Entity.Portfolio;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "featured_trainer")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeaturedTrainer {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String designation;

    private String experience;

    private String skills;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String profileImageUrl;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
