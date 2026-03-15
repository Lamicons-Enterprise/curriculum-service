package com.Lamicons.CurriculumService.Entity.Portfolio;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "featured_student")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeaturedStudent {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String courseCompleted;

    private String placementCompany;

    @Column(columnDefinition = "TEXT")
    private String testimonial;

    private String profileImageUrl;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
